<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<c:set var="titulo" value="Más vendidos"/>
<%@include file="partes/cabecera.jsp"%>

<h1>Productos más vendidos</h1>
<p class="bajada">Suma de las unidades que salieron, de todo el historial.</p>

<div class="bloque">
    <c:choose>
        <c:when test="${empty filas}">
            <p class="vacio">Todavía no hay salidas registradas.</p>
        </c:when>
        <c:otherwise>
            <table>
                <caption>
                    Consulta JPQL: SELECT m.productoIdproducto, SUM(m.cantidad) FROM MovimientoStock m
                    WHERE m.tipo = 'SALIDA' GROUP BY m.productoIdproducto ORDER BY SUM(m.cantidad) DESC
                </caption>
                <thead>
                    <tr>
                        <th scope="col" class="num">#</th>
                        <th scope="col">Producto</th>
                        <th scope="col">Categoría</th>
                        <th scope="col" class="num">Unidades vendidas</th>
                        <th scope="col" class="num">Stock actual</th>
                        <th scope="col">Estado</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="fila" items="${filas}" varStatus="s">
                        <c:set var="p" value="${fila[0]}"/>
                        <tr>
                            <td class="num">${s.count}</td>
                            <td>
                                ${p.nombre}
                                <div class="chico">${p.codigo}</div>
                            </td>
                            <td>${p.categoriaIdcategoria.nombre}</td>
                            <td class="num"><strong>${fila[1]}</strong> ${p.unidad}</td>
                            <td class="num">${p.stock}</td>
                            <td><%@include file="partes/estado.jsp"%></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>

<%@include file="partes/pie.jsp"%>
