<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<c:set var="titulo" value="Compras por proveedor"/>
<%@include file="partes/cabecera.jsp"%>

<h1>Compras por proveedor</h1>
<p class="bajada">
    Cuántas entradas de stock vinieron de cada proveedor. Sale del proveedor que quedó
    guardado en cada movimiento de entrada.
</p>

<div class="bloque">
    <c:choose>
        <c:when test="${empty filas}">
            <p class="vacio">Todavía no hay entradas con proveedor registrado.</p>
        </c:when>
        <c:otherwise>
            <table>
                <caption>
                    Consulta JPQL: SELECT m.proveedorIdproveedor, COUNT(m), SUM(m.cantidad)
                    FROM MovimientoStock m WHERE m.tipo = 'ENTRADA' GROUP BY m.proveedorIdproveedor
                </caption>
                <thead>
                    <tr>
                        <th scope="col">Proveedor</th>
                        <th scope="col">Contacto</th>
                        <th scope="col" class="num">Entradas</th>
                        <th scope="col" class="num">Unidades compradas</th>
                        <th scope="col"></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="fila" items="${filas}">
                        <c:set var="v" value="${fila[0]}"/>
                        <tr>
                            <td>${v.nombre}</td>
                            <td class="chico">${v.telefono} ${v.email}</td>
                            <td class="num">${fila[1]}</td>
                            <td class="num"><strong>${fila[2]}</strong></td>
                            <td>
                                <a class="btn btn-chico" href="${ctx}/EditarProveedor?id=${v.idproveedor}">Ver proveedor</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>

<%@include file="partes/pie.jsp"%>
