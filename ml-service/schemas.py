from pydantic import BaseModel, Field
from typing import List, Optional

class ContainerSchema(BaseModel):
    length: float = Field(..., gt=0)
    width: float = Field(..., gt=0)
    height: float = Field(..., gt=0)

class ItemSchema(BaseModel):
    id: str
    length: float = Field(..., gt=0)
    width: float = Field(..., gt=0)
    height: float = Field(..., gt=0)

class InferenceRequest(BaseModel):
    container: ContainerSchema
    items: List[ItemSchema]

class PlacementSchema(BaseModel):
    item_id: str
    x: float
    y: float
    z: float
    placed: bool
    rotation: str

class InferenceResponse(BaseModel):
    placements: List[PlacementSchema]
    utilization: float
    placed_count: int
    total_count: int