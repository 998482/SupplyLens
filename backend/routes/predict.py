from fastapi import APIRouter, HTTPException
from schemas import PredictRequest, PredictResponse
from ml.model import load_model

router = APIRouter()

@router.post("/api/predict-disruption", response_model=PredictResponse)
def predict_disruption(request: PredictRequest):
    try:
        model = load_model()
        result = model.predict(
            request.origin,
            request.destination,
            request.distance_km,
            request.weather,
            request.traffic_level
        )
        return PredictResponse(**result)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
