CREATE TABLE packing_job (
                             id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             status      VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                             created_at  TIMESTAMP NOT NULL DEFAULT now(),
                             container_l DOUBLE PRECISION NOT NULL,
                             container_w DOUBLE PRECISION NOT NULL,
                             container_h DOUBLE PRECISION NOT NULL,
                             utilization DOUBLE PRECISION
);

CREATE TABLE packing_item (
                              id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              job_id  UUID NOT NULL REFERENCES packing_job(id) ON DELETE CASCADE,
                              name    VARCHAR(100),
                              length  DOUBLE PRECISION NOT NULL,
                              width   DOUBLE PRECISION NOT NULL,
                              height  DOUBLE PRECISION NOT NULL
);

CREATE TABLE placement_result (
                                  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                  job_id      UUID NOT NULL REFERENCES packing_job(id) ON DELETE CASCADE,
                                  item_id     UUID NOT NULL REFERENCES packing_item(id) ON DELETE CASCADE,
                                  x           DOUBLE PRECISION NOT NULL DEFAULT 0,
                                  y           DOUBLE PRECISION NOT NULL DEFAULT 0,
                                  z           DOUBLE PRECISION NOT NULL DEFAULT 0,
                                  placed      BOOLEAN NOT NULL DEFAULT false,
                                  rotation    VARCHAR(20)
);