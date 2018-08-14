# Multimidia_livro_Dominando_Android
Como utilizar camera do aparelho android para tirar fotos, gravar videos que aparecerão na galeria de mídias do aparelho

Nessa parte inicial dos estudos sobre, aprendemos atraves do livro do Nelson Glauber "Dominando Android", como utilizar a camera do aparelho para tirar fotos, gravar videos,
na etapa de hoje, conseguimos:
*//iniciamos o fluxo para tirar uma fotografia.
*//verificamos se possui a permissao de armazenar a imagem no cartao de memoria
*carregar a imagen redimencionada para area que queremos exibi-la ...ja que as fotos da camera sao amiores que a tela do celular.
*//salvar caminho das fotos, redimencionadas...porém ainda ñ consegui enviar para ImagemView.

... enfim realizamos um trabalho simples, mas que devido a nossa experiencia pôde apresentar algumas duvidas em quesitos mais complexos que o que parece, como por exemplo as permissoes de gravar e etc:



Obs: A partir do Android N, versoes apartir da 24 e das versões superiores.
segundo a documentação, não podemos mais passar o caminho direto para outras aplicações, pois elas podem ter a ausência da permissão READ_EXTERNAL_STORAGE, por esse motivo não podemos passar file:// que é o que passamos ao invocarmos o método getExternalFilesDir(null).