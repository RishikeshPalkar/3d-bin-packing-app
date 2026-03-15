from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from schemas import InferenceRequest, InferenceResponse
from packing import run_blf

app = FastAPI(title="Bin Packing ML Service", version="1.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.get("/health")
def health():
    return {"status": "ok", "model_loaded": False, "engine": "BLF heuristic"}


@app.post("/predict", response_model=InferenceResponse)
def predict(request: InferenceRequest):
    container = request.container
    items = request.items

    placements = run_blf(items, container)

    container_volume = container.length * container.width * container.height
    placed_items = [p for p in placements if p.placed]
    placed_count = len(placed_items)

    # Recalculate packed volume using original item dimensions
    item_map = {i.id: i for i in items}
    packed_volume = sum(
        item_map[p.item_id].length * item_map[p.item_id].width * item_map[p.item_id].height
        for p in placed_items
    )

    utilization = round((packed_volume / container_volume) * 100, 2) if container_volume > 0 else 0.0

    return InferenceResponse(
        placements=placements,
        utilization=utilization,
        placed_count=placed_count,
        total_count=len(items)
    )