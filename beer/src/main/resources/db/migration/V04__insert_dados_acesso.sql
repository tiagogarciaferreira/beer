INSERT INTO grupo (nome) VALUES('Administrador');
INSERT INTO grupo (nome) VALUES('Vendedor');

INSERT INTO permissao (nome) VALUES('ROLE_CADASTRAR_CIDADE');
INSERT INTO permissao (nome) VALUES('ROLE_CADASTRAR_USUARIO');
INSERT INTO permissao (nome) VALUES('ROLE_CANCELAR_VENDA');

INSERT INTO grupo_permissao (codigo_grupo, codigo_permissao) VALUES(1,1);
INSERT INTO grupo_permissao (codigo_grupo, codigo_permissao) VALUES(1,2);
INSERT INTO grupo_permissao (codigo_grupo, codigo_permissao) VALUES(1,3);

INSERT INTO grupo_permissao (codigo_grupo, codigo_permissao) VALUES(2,1);
INSERT INTO grupo_permissao (codigo_grupo, codigo_permissao) VALUES(2,2);


--ADMIN
INSERT INTO usuario (nome, email, senha, ativo, data_nascimento) VALUES ('TgfCodes', 'tgfcodes@gmail.com', '$2y$04$JKjMBWItW8z5fdt85msSU.sKdcQOrNB8nMTaVw2MPjlK21ZyPTqsK', 'true', '1996-09-21');

INSERT INTO usuario_grupo (codigo_grupo, codigo_usuario) VALUES(1, 1);
INSERT INTO usuario_grupo (codigo_grupo, codigo_usuario) VALUES(2, 1);

