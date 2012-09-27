PGDMP     2    3    
            p           tiendavirtualDB    9.1.5    9.1.5     �           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                       false            �           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                       false            �           1262    47105    tiendavirtualDB    DATABASE     �   CREATE DATABASE "tiendavirtualDB" WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'es_ES.UTF-8' LC_CTYPE = 'es_ES.UTF-8';
 !   DROP DATABASE "tiendavirtualDB";
             tiendavirtual    false                        2615    2200    public    SCHEMA        CREATE SCHEMA public;
    DROP SCHEMA public;
             postgres    false            �           0    0    SCHEMA public    COMMENT     6   COMMENT ON SCHEMA public IS 'standard public schema';
                  postgres    false    5            �            3079    11721    plpgsql 	   EXTENSION     ?   CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;
    DROP EXTENSION plpgsql;
                  false            �           0    0    EXTENSION plpgsql    COMMENT     @   COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';
                       false    166            �            1259    47106    cliente    TABLE       CREATE TABLE cliente (
    id bigint NOT NULL,
    activo boolean NOT NULL,
    direccion_postal character varying(255),
    nombre_completo character varying(255),
    password character varying(255) NOT NULL,
    username character varying(255) NOT NULL,
    version integer
);
    DROP TABLE public.cliente;
       public         tiendavirtual    false    5            �            1259    47148    hibernate_sequence    SEQUENCE     t   CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 )   DROP SEQUENCE public.hibernate_sequence;
       public       tiendavirtual    false    5            �           0    0    hibernate_sequence    SEQUENCE SET     :   SELECT pg_catalog.setval('hibernate_sequence', 1, false);
            public       tiendavirtual    false    165            �            1259    47114    linea_pedido    TABLE     �   CREATE TABLE linea_pedido (
    id bigint NOT NULL,
    cantidad integer NOT NULL,
    precio real,
    version integer,
    pedido bigint,
    producto bigint,
    CONSTRAINT linea_pedido_cantidad_check CHECK (((cantidad >= 1) AND (cantidad <= 99)))
);
     DROP TABLE public.linea_pedido;
       public         tiendavirtual    false    1942    5            �            1259    47120    pedido    TABLE     �   CREATE TABLE pedido (
    id bigint NOT NULL,
    identificador_pedido character varying(255) NOT NULL,
    total real,
    version integer,
    cliente bigint NOT NULL
);
    DROP TABLE public.pedido;
       public         tiendavirtual    false    5            �            1259    47125    producto    TABLE     Z  CREATE TABLE producto (
    id bigint NOT NULL,
    descripcion character varying(255),
    detalles character varying(255),
    fin timestamp without time zone,
    identificador character varying(255) NOT NULL,
    inicio timestamp without time zone,
    nombre character varying(255) NOT NULL,
    precio real NOT NULL,
    version integer
);
    DROP TABLE public.producto;
       public         tiendavirtual    false    5            �          0    47106    cliente 
   TABLE DATA               f   COPY cliente (id, activo, direccion_postal, nombre_completo, password, username, version) FROM stdin;
    public       tiendavirtual    false    161    1958          �          0    47114    linea_pedido 
   TABLE DATA               P   COPY linea_pedido (id, cantidad, precio, version, pedido, producto) FROM stdin;
    public       tiendavirtual    false    162    1958   *       �          0    47120    pedido 
   TABLE DATA               L   COPY pedido (id, identificador_pedido, total, version, cliente) FROM stdin;
    public       tiendavirtual    false    163    1958   G       �          0    47125    producto 
   TABLE DATA               k   COPY producto (id, descripcion, detalles, fin, identificador, inicio, nombre, precio, version) FROM stdin;
    public       tiendavirtual    false    164    1958   d       �           2606    47113    cliente_pkey 
   CONSTRAINT     K   ALTER TABLE ONLY cliente
    ADD CONSTRAINT cliente_pkey PRIMARY KEY (id);
 >   ALTER TABLE ONLY public.cliente DROP CONSTRAINT cliente_pkey;
       public         tiendavirtual    false    161    161    1959            �           2606    47119    linea_pedido_pkey 
   CONSTRAINT     U   ALTER TABLE ONLY linea_pedido
    ADD CONSTRAINT linea_pedido_pkey PRIMARY KEY (id);
 H   ALTER TABLE ONLY public.linea_pedido DROP CONSTRAINT linea_pedido_pkey;
       public         tiendavirtual    false    162    162    1959            �           2606    47124    pedido_pkey 
   CONSTRAINT     I   ALTER TABLE ONLY pedido
    ADD CONSTRAINT pedido_pkey PRIMARY KEY (id);
 <   ALTER TABLE ONLY public.pedido DROP CONSTRAINT pedido_pkey;
       public         tiendavirtual    false    163    163    1959            �           2606    47132    producto_pkey 
   CONSTRAINT     M   ALTER TABLE ONLY producto
    ADD CONSTRAINT producto_pkey PRIMARY KEY (id);
 @   ALTER TABLE ONLY public.producto DROP CONSTRAINT producto_pkey;
       public         tiendavirtual    false    164    164    1959            �           2606    47138    fk89ec90d71d47a1cc    FK CONSTRAINT     p   ALTER TABLE ONLY linea_pedido
    ADD CONSTRAINT fk89ec90d71d47a1cc FOREIGN KEY (pedido) REFERENCES pedido(id);
 I   ALTER TABLE ONLY public.linea_pedido DROP CONSTRAINT fk89ec90d71d47a1cc;
       public       tiendavirtual    false    162    163    1947    1959            �           2606    47133    fk89ec90d76e554d82    FK CONSTRAINT     t   ALTER TABLE ONLY linea_pedido
    ADD CONSTRAINT fk89ec90d76e554d82 FOREIGN KEY (producto) REFERENCES producto(id);
 I   ALTER TABLE ONLY public.linea_pedido DROP CONSTRAINT fk89ec90d76e554d82;
       public       tiendavirtual    false    162    164    1949    1959            �           2606    47143    fkc4dd174544b800f2    FK CONSTRAINT     l   ALTER TABLE ONLY pedido
    ADD CONSTRAINT fkc4dd174544b800f2 FOREIGN KEY (cliente) REFERENCES cliente(id);
 C   ALTER TABLE ONLY public.pedido DROP CONSTRAINT fkc4dd174544b800f2;
       public       tiendavirtual    false    163    161    1943    1959            �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �     