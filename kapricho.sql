-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Servidor: localhost
-- Tiempo de generación: 20-02-2022 a las 23:37:05
-- Versión del servidor: 10.4.21-MariaDB
-- Versión de PHP: 7.4.27

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `kapricho`
--

DELIMITER $$
--
-- Procedimientos
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `add_carrito` (IN `codigo` INT, IN `cantidad` INT, IN `token_user` VARCHAR(50))  BEGIN
    	DECLARE precio_actual decimal(10,2);
        SELECT precio INTO precio_actual FROM producto WHERE id_producto = codigo;
        
        INSERT INTO carrito(token_user, id_producto_carrito, cantidad_carrito, total_carrito) VALUES(token_user,codigo,cantidad,precio_actual);
        
        SELECT ca.id_carrito, ca.id_producto_carrito,p.descripcion,ca.cantidad_carrito,ca.total_carrito FROM carrito ca 
        INNER JOIN producto p
        ON ca.id_producto_carrito = p.id_producto
        WHERE ca.token_user = token_user;
    END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `anular_venta` (IN `cod_venta` INT)  BEGIN
        	DECLARE existe_pedido INT;
            
            SET existe_pedido = (SELECT COUNT(*) FROM pedido WHERE id_pedido = cod_venta);
            
            IF existe_pedido > 0 THEN
            	UPDATE pedido SET estado = "Anulado", estado_cocina = "Devuelto" WHERE id_pedido = cod_venta;
                SELECT * FROM pedido WHERE id_pedido = cod_venta;
            ELSE
            	SELECT 0 pedido;
            END IF;
    	END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `cobro_pedido` (IN `cod_venta` INT, IN `medio_pago` VARCHAR(50))  BEGIN
    	DECLARE existe_pedido INT;
            
            SET existe_pedido = (SELECT COUNT(*) FROM pedido WHERE id_pedido = cod_venta);
            
            IF existe_pedido > 0 THEN
            	UPDATE pedido SET estado = "Pagado", medio_de_pago = medio_pago WHERE id_pedido = cod_venta;
                SELECT * FROM pedido WHERE id_pedido = cod_venta;
            ELSE
            	SELECT 0 pedido;
            END IF;
        
    END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `procesar_pedido` (IN `cod_usuario` INT, IN `cod_cliente` INT, IN `token` VARCHAR(50), IN `mesa_` INT)  BEGIN
    	DECLARE factura int;
        
        DECLARE registros int;
        DECLARE total_temp DECIMAL(10,2);
        
        CREATE TEMPORARY TABLE tbl_tmp_tokenuser (
            id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
            cod_prod BIGINT,
            cant_prod int);
            
        SET registros = (SELECT COUNT(*) FROM carrito WHERE token_user = token);
        
        IF registros > 0 THEN
        	INSERT INTO tbl_tmp_tokenuser(cod_prod,cant_prod) SELECT id_producto_carrito,cantidad_carrito FROM carrito WHERE token_user = token;
            
            INSERT INTO pedido(id_usuario, id_cliente, mesa) VALUES(cod_usuario, cod_cliente, mesa_);
            SET factura = LAST_INSERT_ID();
            
            INSERT INTO detalle_pedido(id_pedido, id_producto, cantidad, total) SELECT (factura) AS nopedido, id_producto_carrito, cantidad_carrito, total_carrito FROM carrito WHERE token_user = token;
            
            SET total_temp = (SELECT SUM(cantidad_carrito * total_carrito) FROM carrito WHERE token_user = token);
            UPDATE pedido SET total = total_temp WHERE id_pedido = factura;
            DELETE FROM carrito WHERE token_user = token;
            TRUNCATE TABLE tbl_tmp_tokenuser;
            SELECT * FROM pedido WHERE id_pedido = factura;
        ELSE
        	SELECT 0;
        END IF;
    END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `carrito`
--

CREATE TABLE `carrito` (
  `id_carrito` int(11) NOT NULL,
  `id_producto_carrito` int(11) NOT NULL,
  `cantidad_carrito` double NOT NULL,
  `total_carrito` decimal(10,2) NOT NULL,
  `token_user` varchar(255) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cliente`
--

CREATE TABLE `cliente` (
  `Id_cliente` int(11) NOT NULL,
  `nombre_cliente` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `apellido_cliente` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `usuario_cliente` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `clave_cliente` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `correo_cliente` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `rol` varchar(50) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'Cliente'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Volcado de datos para la tabla `cliente`
--

INSERT INTO `cliente` (`Id_cliente`, `nombre_cliente`, `apellido_cliente`, `usuario_cliente`, `clave_cliente`, `correo_cliente`, `rol`) VALUES
(2, 'Leopoldo', 'Mejia', 'leomejia', '1234', 'leomejia@yopmail.com', 'Cliente'),
(3, 'Lucho', 'Diaz', 'luchodiaz', '1234', 'luchodiaz@yopmail.com', 'Cliente'),
(4, 'Alberto', 'Medina', 'albermedina', '1234', 'albermedina@yopmail.com', 'Cliente');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `detalle_pedido`
--

CREATE TABLE `detalle_pedido` (
  `id_pedido_detalle` int(11) NOT NULL,
  `id_pedido` int(11) NOT NULL,
  `id_producto` int(11) NOT NULL,
  `cantidad` double NOT NULL,
  `total` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Volcado de datos para la tabla `detalle_pedido`
--

INSERT INTO `detalle_pedido` (`id_pedido_detalle`, `id_pedido`, `id_producto`, `cantidad`, `total`) VALUES
(28, 16, 18, 1, 8000),
(29, 17, 19, 3, 15000),
(30, 17, 16, 2, 12000),
(31, 17, 18, 1, 8000),
(32, 18, 23, 1, 13500),
(33, 18, 21, 5, 12900),
(34, 18, 16, 11, 12000),
(35, 18, 18, 7, 8000),
(36, 18, 15, 3, 2000);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pedido`
--

CREATE TABLE `pedido` (
  `id_pedido` int(11) NOT NULL,
  `id_cliente` int(11) NOT NULL,
  `id_usuario` int(11) NOT NULL,
  `fecha` datetime NOT NULL DEFAULT current_timestamp(),
  `mesa` int(11) NOT NULL,
  `total` decimal(10,2) DEFAULT NULL,
  `estado` varchar(50) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'Sin pagar',
  `medio_de_pago` varchar(50) COLLATE utf8_unicode_ci DEFAULT 'Sin pagar',
  `estado_cocina` varchar(50) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'Recibido'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Volcado de datos para la tabla `pedido`
--

INSERT INTO `pedido` (`id_pedido`, `id_cliente`, `id_usuario`, `fecha`, `mesa`, `total`, `estado`, `medio_de_pago`, `estado_cocina`) VALUES
(16, 3, 3, '2022-02-19 15:46:40', 1, '8000.00', 'Anulado', 'Transaccion', 'Devuelto'),
(17, 2, 2, '2022-02-20 15:08:16', 3, '77000.00', 'Pagado', 'Efectivo', 'Entregado'),
(18, 2, 2, '2023-02-20 15:33:27', 10, '272000.00', 'Pagado', 'Efectivo', 'Entregado');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `producto`
--

CREATE TABLE `producto` (
  `id_producto` int(11) NOT NULL,
  `precio` double NOT NULL,
  `descripcion` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `imagen` mediumtext COLLATE utf8_unicode_ci NOT NULL,
  `existencia` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `categoria` varchar(60) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Volcado de datos para la tabla `producto`
--

INSERT INTO `producto` (`id_producto`, `precio`, `descripcion`, `imagen`, `existencia`, `categoria`) VALUES
(15, 2000, 'Adicion de papitas a la francesa', 'imgProductos/02-08-202210:54:35pm.jpeg', 'Disponible', 'Adicion'),
(16, 12000, 'Arroz de pollo', 'imgProductos/02-16-202201:31:44pm.jpeg', 'Disponible', 'Almuerzo'),
(18, 8000, 'Mote de queso', 'imgProductos/02-16-202201:29:11pm.jpeg', 'Agotado', 'Almuerzo'),
(19, 15000, 'Filete de carne con papa cocida y ensalada', 'imgProductos/02-16-202201:29:50pm.jpeg', 'Disponible', 'Almuerzo'),
(20, 14000, 'Hamburguesa con carne rellena de queso', 'imgProductos/02-16-202201:32:09pm.jpeg', 'Disponible', 'Almuerzo'),
(21, 12900, 'Pankakes con huevo y tocino', 'imgProductos/02-16-202201:32:58pm.jpeg', 'Disponible', 'Desayuno'),
(22, 11500, 'Pan con huevo y salchichas', 'imgProductos/02-16-202201:33:27pm.jpeg', 'Disponible', 'Desayuno'),
(23, 13500, 'Milo con pan queso dulce', 'imgProductos/02-16-202201:34:55pm.jpeg', 'Disponible', 'Desayuno');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id_usuario` int(11) NOT NULL,
  `nombre` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `apellido` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `usuario` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `clave` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `rol` varchar(50) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id_usuario`, `nombre`, `apellido`, `usuario`, `clave`, `rol`) VALUES
(1, 'Administracion', '', 'admin', 'admin', 'Administrador'),
(11, 'Rafael', 'Nadal', 'rafanadal', '1234', 'Cocina');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `carrito`
--
ALTER TABLE `carrito`
  ADD PRIMARY KEY (`id_carrito`),
  ADD KEY `id_producto_carrito` (`id_producto_carrito`);

--
-- Indices de la tabla `cliente`
--
ALTER TABLE `cliente`
  ADD PRIMARY KEY (`Id_cliente`);

--
-- Indices de la tabla `detalle_pedido`
--
ALTER TABLE `detalle_pedido`
  ADD PRIMARY KEY (`id_pedido_detalle`),
  ADD KEY `id_producto` (`id_producto`),
  ADD KEY `id_pedido` (`id_pedido`);

--
-- Indices de la tabla `pedido`
--
ALTER TABLE `pedido`
  ADD PRIMARY KEY (`id_pedido`),
  ADD KEY `id_cliente` (`id_cliente`),
  ADD KEY `id_usuario` (`id_usuario`);

--
-- Indices de la tabla `producto`
--
ALTER TABLE `producto`
  ADD PRIMARY KEY (`id_producto`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id_usuario`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `carrito`
--
ALTER TABLE `carrito`
  MODIFY `id_carrito` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=63;

--
-- AUTO_INCREMENT de la tabla `cliente`
--
ALTER TABLE `cliente`
  MODIFY `Id_cliente` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `detalle_pedido`
--
ALTER TABLE `detalle_pedido`
  MODIFY `id_pedido_detalle` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=46;

--
-- AUTO_INCREMENT de la tabla `pedido`
--
ALTER TABLE `pedido`
  MODIFY `id_pedido` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- AUTO_INCREMENT de la tabla `producto`
--
ALTER TABLE `producto`
  MODIFY `id_producto` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id_usuario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `carrito`
--
ALTER TABLE `carrito`
  ADD CONSTRAINT `carrito_ibfk_2` FOREIGN KEY (`id_producto_carrito`) REFERENCES `producto` (`id_producto`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `detalle_pedido`
--
ALTER TABLE `detalle_pedido`
  ADD CONSTRAINT `detalle_pedido_ibfk_1` FOREIGN KEY (`id_producto`) REFERENCES `producto` (`id_producto`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `detalle_pedido_ibfk_2` FOREIGN KEY (`id_pedido`) REFERENCES `pedido` (`id_pedido`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `pedido`
--
ALTER TABLE `pedido`
  ADD CONSTRAINT `pedido_ibfk_1` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`Id_cliente`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
