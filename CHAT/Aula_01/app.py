import streamlit as st
from langchain_groq import ChatGroq
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import StrOutputParser
from langchain_core.messages import AIMessage, HumanMessage
from dotenv import load_dotenv

load_dotenv()

st.set_page_config(
    page_title="TechStore Assistente 🤖",
    page_icon="💻"
)

st.title("💻 TechStore - Assistente Virtual")

id_modelo = "openai/gpt-oss-20b"


def load_llm():
    return ChatGroq(
        model=id_modelo,
        temperature=0.3,
        max_retries=2,
        max_tokens=None,
        timeout=None
    )


llm = load_llm()

def responder_usuario(pergunta):
    template = ChatPromptTemplate.from_messages(
        [("system", "Você é um assistente especialista em programação"),
        ("human", "{pergunta}")]
    )
    chain = template | llm

    resposta = chain.invoke({
        "pergunta": pergunta
    })

    return resposta

user = "Qual o conceito de listas em Python?"
resp = responder_usuario(pergunta=user)
print(resp)