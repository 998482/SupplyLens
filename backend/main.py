from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from routes.predict import router as predict_router
from routes.dashboard import router as dashboard_router
from routes.cascade import router as cascade_router
from routes.ai_routes import router as ai_router

app = FastAPI(title="SupplyLens API", version="1.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(predict_router)
app.include_router(dashboard_router)
app.include_router(cascade_router)
app.include_router(ai_router)

@app.get("/")
def root():
    return {"status": "SupplyLens API running", "version": "1.0.0"}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
