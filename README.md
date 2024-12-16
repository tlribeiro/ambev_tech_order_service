# Ambev Tech - Java Developer - Test

### Visão Geral
Este teste tem como objetivo abordar alguns caminhos possíveis para ter uma aplicação mais robusta.

Para desenvolvimento foi escolhida a Arquitetura Hexagonal para isolar a camada de negócio e também adaptar de forma fácil possíveis mudanças de tenologias ou abordagens.

Acredito que podemos discutir alguns pontos da solução, mas a ideia é mostrar algumas opções.


### Tecnologias
As tecnologias utilizadas no projeto foram escolhidas para aumentar a capacidade de atendender um número maior de requests e também conseguir distribuir a informação por demanda.

Para tirar proveito de toda insfra-estrutura fornecida pelo WebFlux, com a programação reativa, foi escolhido um banco de dados que tivesse a mesma capaciedade de trabalhar com essa tecnologia.

* Spring boot: 3.3.6
* Web Flux
* Mongo DB
* Google PUB / SUB

### Ambiente local
Foi adicionado ao projeto um Docker Compose que cria a estrutura para desenvolvimento local.

É criado o banco de dados MongoDb e também o emulador do Google PUB/SUB.

Para desenvolvimento é necessário rodar com o PROFILE LOCAL.

Exemplo: -Dspring.profiles.active=local

### Pedidos
Foi estruturado a API de Pedidos com as rotas/recursos padrão de uma API Rest.

#### Rotas Disponíveis
* /api/orders - Método GET - Lista todos os pedidos
  * Pedidos paginados
* /api/orders - Método POST - Cria um Pedido
  * Sucesso: Status Code 201 + header location
  * Pedido Duplicado: Status Code 422 + estrutura de erro
* /api/orders/<id mondo db> - Método POST - Cria um Pedido
  * Sucesso: Status code 200
  * Não encontrato: Status code 404

### Produto Externo B - Disponibilização Pedido
Existem algumas formas de disponibilizar os pedidos para o Produco Externo B.

#### API Pedidos - Listagem
A primeira delas e mais simples é a disponbilização da API Listar Pedidos, onde o próprio Produto Externo B consegue fazer a consulta no momento que preferir.
Uma das desvantagens é ficar fazendo polling de tempos em tempos para validar se existem novos dados disponíveis.

#### Notificação por demanda
Pensando em um cenário mais perto do ideal, e possível em um ambiente comporaativo, podemos realizar a notificação do Produto Externo B por demanda.
Sempre que serviço OrderService recebe um novo pedido ele realiza o calculo e realiza uma notificação.

Tipos de Notificações
1) Chamada do Produto Externo B: Chama a API direto do Produto Externo B, porém gera um alto acoplamento dos serviços.
2) Mensageria: Gera um evento e os serviços que tiverem interesse podem receber este evento. Está é uma abordagem onde temos um baixo acoplamento dos serviços.

A segunda abordagem depende da infra-estrutura disponível e também se o Produto Externo B pode ter ou não acesso a este serviço.

Foi gerado um Port de Saída chamado OrderListenerOutputPort, que conforme a estratégia pode ser adaptado a implementação.


### Pedidos Por Demanda
Em um cenário onde cresça essa demanda de pedidos, também é possível receber e processar estes pedidos por demanda.
Desta forma existe um fila disponível e quem precisar gerar pedidos pode colocar a mensagem nesta fila.

Para teste local está disponível a rota
* api/producers/orders: Recebe o pedido e retorna o Status Code 202 - Accepted


### Testes
* Criado os testes unitários para as principais classes do sistema.
* Disponibilizado uma collection do postman para testes
* Swagger para acesso as APIS.





