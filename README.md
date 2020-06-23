# Benchmark Dynomite-Redis

### Uma aplicação Java desenvolvida para fazer testes de cargas no Dynomite tendo como o backend o Redis.

O _DynomitDB_ é um projeto dos engenheiros da Netflix formado de três componentes: o _Dynomite_ que é uma camada que fornece alta disponibilidade, fragmentação e replicação de dados, um _backend_ que nesse caso é o _Redis_ e o _Dyno_, um cliente Java que se comunica com o _Dynomite_ utilizando os protocolos do _Redis_.
Fonte: http://www.dynomitedb.com/

O _Redis_ é um banco de dados em memória que suporta o armazenamento dos seguintes tipos de dados: _strings_, _hashes_, _lists_, _sets_, _sorted sets_, _bitmaps_, _hyperloglogs_, _geospatial indexes_ e _streams_. O fato de o armazenamento ser em memória torna o _Redis_ muito atrativo pela velocidade de operações de escrita e leituras de dados.
Fonte: https://redis.io/

O _Cluster Dynomite_ foi criado utilizando máquinas virtuais. Para simulação de vários clientes a aplicação dispara várias _threads_ que concorrem simultaneamente pelos recursos do _cluster_.
