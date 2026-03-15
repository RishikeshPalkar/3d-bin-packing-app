from schemas import ContainerSchema, ItemSchema, PlacementSchema
from typing import List, Tuple


def _overlaps(ax, ay, az, al, aw, ah, bx, by, bz, bl, bw, bh) -> bool:
    return (
            ax < bx + bl and ax + al > bx and
            ay < by + bh and ay + ah > by and
            az < bz + bw and az + aw > bz
    )


def _fits_in_container(x, y, z, l, w, h, cl, cw, ch) -> bool:
    return x + l <= cl and y + h <= ch and z + w <= cw


def _try_place(item: ItemSchema,
               placed: List[dict],
               container: ContainerSchema) -> Tuple[bool, float, float, float, str]:
    cl, cw, ch = container.length, container.width, container.height

    # Generate candidate rotations (l, w, h) — 6 orientations
    l, w, h = item.length, item.width, item.height
    rotations = [
        (l, w, h, "LWH"),
        (l, h, w, "LHW"),
        (w, l, h, "WLH"),
        (w, h, l, "WHL"),
        (h, l, w, "HLW"),
        (h, w, l, "HWL"),
    ]

    # Candidate positions: origin + corners of every placed box
    candidate_xs = [0.0] + [p["x"] + p["l"] for p in placed]
    candidate_ys = [0.0] + [p["y"] + p["h"] for p in placed]
    candidate_zs = [0.0] + [p["z"] + p["w"] for p in placed]

    best = None  # (y, x, z, rl, rw, rh, rot_name)

    for rx, ry, rz in [
        (x, y, z)
        for x in sorted(set(candidate_xs))
        for y in sorted(set(candidate_ys))
        for z in sorted(set(candidate_zs))
    ]:
        for rl, rw, rh, rot_name in rotations:
            if not _fits_in_container(rx, ry, rz, rl, rw, rh, cl, cw, ch):
                continue
            collision = any(
                _overlaps(rx, ry, rz, rl, rw, rh,
                          p["x"], p["y"], p["z"], p["l"], p["w"], p["h"])
                for p in placed
            )
            if collision:
                continue
            # Score: prefer low y (gravity), then low x, then low z
            score = (ry, rx, rz)
            if best is None or score < best[0]:
                best = (score, rx, ry, rz, rl, rw, rh, rot_name)

    if best is None:
        return False, 0.0, 0.0, 0.0, "NONE"

    _, px, py, pz, rl, rw, rh, rot_name = best
    return True, px, py, pz, rot_name


def run_blf(request_items: List[ItemSchema],
            container: ContainerSchema) -> List[PlacementSchema]:
    # Sort by volume descending (largest first — better packing)
    sorted_items = sorted(
        request_items,
        key=lambda i: i.length * i.width * i.height,
        reverse=True
    )

    placed_meta: List[dict] = []
    results: List[PlacementSchema] = []

    for item in sorted_items:
        success, x, y, z, rotation = _try_place(item, placed_meta, container)

        if success:
            l = item.length
            w = item.width
            h = item.height
            # Apply rotation to actual dimensions stored
            rot_dims = {
                "LWH": (item.length, item.width, item.height),
                "LHW": (item.length, item.height, item.width),
                "WLH": (item.width, item.length, item.height),
                "WHL": (item.width, item.height, item.length),
                "HLW": (item.height, item.length, item.width),
                "HWL": (item.height, item.width, item.length),
            }
            rl, rw, rh = rot_dims.get(rotation, (l, w, h))
            placed_meta.append({"x": x, "y": y, "z": z, "l": rl, "w": rw, "h": rh})

        results.append(PlacementSchema(
            item_id=item.id,
            x=round(x, 4),
            y=round(y, 4),
            z=round(z, 4),
            placed=success,
            rotation=rotation
        ))

    return results