# O Gson lê os campos por reflexão: não deixe o R8 renomear o modelo.
-keep class br.edu.unisenai.chefdebolso.model.** { *; }
