<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<c:set var="titulo" value="Hay que reponer"/>
<%@include file="partes/cabecera.jsp"%>

<h1>Hay que reponer</h1>
<p class="bajada">
    Productos con el stock igual o por debajo de su mínimo, ordenados por urgencia.
    El objetivo de reposición es el doble del mínimo, y el proveedor sugerido es el más barato
    de los que venden ese producto.
</p>

<div class="bloque">
    <c:choose>
        <c:when test="${empty productos}">
            <p class="vacio">Ningún producto está por debajo de su stock mínimo. Todo en orden.</p>
        </c:when>
        <c:otherwise>
            <table>
                <caption>
                    Consulta JPQL: SELECT p FROM Producto p WHERE p.stock &lt;= p.stockMinimo ORDER BY p.stock, p.nombre
                </caption>
                <thead>
                    <tr>
                        <th scope="col">Producto</th>
                        <th scope="col">Estado</th>
                        <th scope="col" class="num">Stock / mínimo</th>
                        <th scope="col" class="num">Faltan</th>
                        <th scope="col">Proveedor sugerido</th>
                        <th scope="col" class="num">Costo estimado</th>
                        <th scope="col"></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="p" items="${productos}">
                        <c:set var="objetivo" value="${p.stockMinimo gt 0 ? p.stockMinimo * 2 : 1}"/>
                        <c:set var="faltante" value="${objetivo - p.stock gt 0 ? objetivo - p.stock : 1}"/>
                        <c:set var="mejor" value="${mejores[p.idproducto]}"/>
                        <tr class="${p.stock eq 0 ? 'fila-agotada' : 'fila-alerta'}">
                            <td>
                                ${p.nombre}
                                <div class="chico">${p.codigo} &middot; ${p.categoriaIdcategoria.nombre}</div>
                            </td>
                            <td><%@include file="partes/estado.jsp"%></td>
                            <td class="num">
                                ${p.stock} / ${p.stockMinimo}
                                <%@include file="partes/barra.jsp"%>
                            </td>
                            <td class="num"><strong>${faltante}</strong> ${p.unidad}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${empty mejor}">
                                        <a class="chico" href="${ctx}/ProveedoresProducto?id=${p.idproducto}">Sin proveedor cargado</a>
                                    </c:when>
                                    <c:otherwise>
                                        ${mejor.proveedor.nombre}
                                        <div class="chico">
                                            <fmt:formatNumber value="${mejor.precioCompra}" type="currency" currencySymbol="$"/>
                                            por ${p.unidad}<c:if test="${not empty mejor.diasEntrega}">, entrega en ${mejor.diasEntrega} días</c:if>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="num">
                                <c:choose>
                                    <c:when test="${empty mejor}">&mdash;</c:when>
                                    <c:otherwise>
                                        <fmt:formatNumber value="${faltante * mejor.precioCompra}" type="currency" currencySymbol="$" maxFractionDigits="0"/>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <a class="btn btn-chico btn-primario"
                                   href="${ctx}/RegistrarMovimiento?producto=${p.idproducto}&amp;tipo=ENTRADA&amp;cantidad=${faltante}<c:if test="${not empty mejor}">&amp;proveedor=${mejor.proveedor.idproveedor}</c:if>">Reponer</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>

<%@include file="partes/pie.jsp"%>
