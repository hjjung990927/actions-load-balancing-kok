# uvicorn main:app --reload
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.naive_bayes import MultinomialNB
from sklearn.pipeline import Pipeline
import joblib

app = FastAPI()

origins = [
    '*'
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"]
)

@app.get("/")
async def root():
    return {"message": "Hello World"}


@app.get("/hello/{name}")
async def say_hello(name: str):
    return {"message": f"Hello {name}"}


# @app.get("/api/spam-check/{message}")
# async def check_spam(message: str):
#     print(message)
#     model = joblib.load("spam_model.pkl")
#     return {'isSpam': bool(model.predict([message])[0])}

class WordCheckRequest(BaseModel):
    message: str

class WordCheckResponse(BaseModel):
    isBadWord: bool


@app.post("/api/community/word-check", response_model=WordCheckResponse)
async def check_word(request: WordCheckRequest):
    model = joblib.load(f"word_model.pkl")
    prediction = model.predict([request.message])
    return {"isBadWord": bool(prediction)}









