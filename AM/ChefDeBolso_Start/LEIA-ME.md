# Chef de Bolso

Chat de culinária em Java que conversa com o Gemini pelo Firbase AI Logic. A resposta chega aos pedaços (streaming), o chef tem persona definida por instrução de sistema e o botão "Receita com o que tenho" pede ao modelo um JSON que vira uma bolha de receita no RecyclerView.

## Antes de abrir no Android Studio

O projeto não roda sem o `google-services.json`. Ele não vem no zip (está no `.gitignore`), cada dupla baixa o seu.

1. No console do Firebase, abra o projeto que vocês usaram no Rango na Régua ou crie um novo. Não precisa ativar o Google Analytics.
2. Registre um app Android com o pacote `br.edu.unisenai.chefdebolso` e baixe o `google-services.json` para dentro de `app/`.
3. Vá em **AI Services > AI Logic** e clique em **Get started**. Escolha **Gemini Developer API**. É a opção sem cobrança, funciona no plano Spark.
4. Continue o assistente até o fim. Ele liga as APIs necessárias e já deixa o App Check **obrigatório** para o AI Logic. 
6. Abra o Logcat, filtre por `DebugAppCheckProvider` e copie o token da linha que diz "Enter this debug secret into the allow list".
7. No console: **Security > App Check > Apps**, menu de três pontos do app, **Manage debug tokens**, cole o token e salve.
8. Rode de novo. Agora o chef responde.

O token de depuração vale para aquele emulador com aquela instalação. Se apagar os dados do app, desinstalar ou trocar de emulador, nasce outro token e o passo 6 se repete.

## Onde está cada coisa

| Arquivo | O que faz |
|---|---|
| `ChefApp.java` | Inicializa o Firebase e instala o App Check de depuração. Roda antes de qualquer tela. |
| `Config.java` | Nome do modelo e as duas instruções de sistema (chat e receita). |
| `ia/ChefIA.java` | Toda a conversa com o Gemini: chat com streaming, histórico, schema e receita em JSON. |
| `ia/TradutorDeErros.java` | Troca a exceção do SDK por uma frase que o usuário entende. A exceção completa vai para o Logcat com a tag `ChefIA`. |
| `model/Mensagem.java` | Uma bolha da conversa. O tipo decide o layout. |
| `model/Receita.java` | Espelho do schema. O Gson casa os campos pelo nome. |
| `ui/MensagemAdapter.java` | O Adapter da semana 5, com um layout (`item_mensagem.xml`) e um ViewHolder. O tipo da mensagem só muda o lado, a cor e o texto da bolha. |
| `MainActivity.java` | Tela única, rotação com `onSaveInstanceState`, menu "Nova conversa". |


## Limites que vale saber

O plano gratuito da Gemini Developer API tem limite de requisições por minuto e por dia, contado por projeto. Se a turma inteira usar o mesmo projeto, o erro 429 aparece rápido. Por isso cada um usa o seu.

Girar a tela no meio de uma resposta corta o streaming: a Activity antiga morre e leva o `Subscriber` junto. A bolha fica marcada como "(resposta interrompida)". Isso só resolve com o  ViewModel que ainda vamos aprender.

O App Check de depuração serve só para desenvolvimento. Um app publicado usaria o Play Integrity, que exige o app na Play Console e fica fora do escopo da nossa disciplina.
