CREATE TABLE "cerveja" (
  "codigo" serial8,
  "sku" varchar(10) NOT NULL,
  "nome" varchar(50) NOT NULL,
  "descricao" varchar(100) NOT NULL,
  "codigo_estilo" int8 NOT NULL,
  "sabor" varchar(50) NOT NULL DEFAULT '',
  "origem" varchar(50) NOT NULL,
  "valor" numeric(10,2) NOT NULL,
  "teor_alcoolico" numeric(10,2) NOT NULL,
  "comissao" numeric(10,2) NOT NULL,
  "quantidade" int4 NOT NULL,
  "foto" varchar(255),
  "content_type" varchar(10),
  PRIMARY KEY ("codigo")
);

CREATE TABLE "cidade" (
  "codigo" serial8,
  "nome" varchar(100) NOT NULL,
  "codigo_estado" int8 NOT NULL,
  PRIMARY KEY ("codigo")
);

CREATE TABLE "cliente" (
  "codigo" serial8,
  "nome" varchar(100) NOT NULL,
  "tipo_pessoa" varchar(20) NOT NULL,
  "cpf_cnpj" varchar(30) NOT NULL,
  "telefone" varchar(20) NOT NULL,
  "email" varchar(50) NOT NULL,
  "logradouro" varchar(100) NOT NULL,
  "numero" varchar(10) NOT NULL,
  "complemento" varchar(50),
  "cep" varchar(20) NOT NULL,
  "codigo_cidade" int8 NOT NULL,
  PRIMARY KEY ("codigo")
);

CREATE TABLE "estado" (
  "codigo" serial8,
  "nome" varchar(100) NOT NULL,
  "sigla" varchar(5) NOT NULL,
  PRIMARY KEY ("codigo")
);

CREATE TABLE "estilo" (
  "codigo" serial8,
  "nome" varchar(100) NOT NULL,
  PRIMARY KEY ("codigo")
);

CREATE TABLE "grupo" (
  "codigo" serial8,
  "nome" varchar(50) NOT NULL,
  PRIMARY KEY ("codigo")
);

CREATE TABLE "grupo_permissao" (
  "codigo_grupo" int8 NOT NULL,
  "codigo_permissao" int8 NOT NULL
);

CREATE TABLE "permissao" (
  "codigo" serial8,
  "nome" varchar(50) NOT NULL,
  PRIMARY KEY ("codigo")
);

CREATE TABLE "usuario" (
  "codigo" serial8,
  "nome" varchar(100) NOT NULL,
  "email" varchar(50) NOT NULL,
  "senha" varchar(100) NOT NULL,
  "ativo" bool NOT NULL,
  "data_nascimento" date NOT NULL,
  PRIMARY KEY ("codigo")
);

CREATE TABLE "usuario_grupo" (
  "codigo_grupo" int8 NOT NULL,
  "codigo_usuario" int8 NOT NULL
);

CREATE TABLE "venda" (
  "codigo" serial8,
  "data_criacao" timestamp NOT NULL,
  "valor_frete" numeric(10,2) NOT NULL,
  "valor_desconto" numeric(10,2) NOT NULL,
  "valor_total" numeric(10,2) NOT NULL,
  "status" varchar(20) NOT NULL,
  "observacao" varchar(255),
  "data_hora_entrega" timestamp NOT NULL,
  "codigo_cliente" int8 NOT NULL,
  "codigo_usuario" int8 NOT NULL,
  PRIMARY KEY ("codigo")
);

CREATE TABLE "item_venda" (
  "codigo" serial8,
  "quantidade" int4 NOT NULL,
  "valor_unitario" numeric(10,2) NOT NULL,
  "codigo_cerveja" int8 NOT NULL,
  "codigo_venda" int8 NOT NULL,
  PRIMARY KEY ("codigo")
);

ALTER TABLE "cerveja" ADD CONSTRAINT "fk_estilo_to_cerveja" FOREIGN KEY ("codigo_estilo") REFERENCES "estilo" ("codigo") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "cidade" ADD CONSTRAINT "fk_estado_to_cidade" FOREIGN KEY ("codigo_estado") REFERENCES "estado" ("codigo") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "cliente" ADD CONSTRAINT "fk_cidade_to_cliente" FOREIGN KEY ("codigo_cidade") REFERENCES "cidade" ("codigo") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "grupo_permissao" ADD CONSTRAINT "fk_grupo_to_grupo_permissao" FOREIGN KEY ("codigo_grupo") REFERENCES "grupo" ("codigo") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "grupo_permissao" ADD CONSTRAINT "fk_permissao_to_grupo_permissao" FOREIGN KEY ("codigo_permissao") REFERENCES "permissao" ("codigo") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "usuario_grupo" ADD CONSTRAINT "fk_usuario_to_usuario_grupo" FOREIGN KEY ("codigo_usuario") REFERENCES "usuario" ("codigo") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "usuario_grupo" ADD CONSTRAINT "fk_grupo_to_usuario_grupo" FOREIGN KEY ("codigo_grupo") REFERENCES "grupo" ("codigo") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "item_venda" ADD CONSTRAINT "fk_venda_to_item_venda" FOREIGN KEY ("codigo_venda") REFERENCES "venda" ("codigo") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "item_venda" ADD CONSTRAINT "fk_cerveja_to_item_venda" FOREIGN KEY ("codigo_cerveja") REFERENCES "cerveja" ("codigo") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "venda" ADD CONSTRAINT "fk_cliente_to_venda" FOREIGN KEY ("codigo_cliente") REFERENCES "cliente" ("codigo") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "venda" ADD CONSTRAINT "fk_usuario_to_venda" FOREIGN KEY ("codigo_usuario") REFERENCES "usuario" ("codigo") ON DELETE RESTRICT ON UPDATE CASCADE;

