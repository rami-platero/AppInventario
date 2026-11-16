<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<c:set var="titulo" value="Panel"/>
<%@include file="partes/cabecera.jsp"%>

<h1>Panel</h1>
<p class="bajada">Estado del inventario y últimos movimientos registrados.</p>

<div class="tarjetas">
    <div class="tarjeta">
        <div class="rotulo">Productos</div>
        <div class="valor">${cantidadProductos}</div>
        <div class="nota">en ${cantidadCategorias} categorías</div>
    </div>
    <div class="tarjeta">
        <div class="rotulo">Unidades en stock</div>
        <div class="valor">${totalUnidades}</div>
        <div class="nota">sumando todos los productos</div>
    </div>
    <div class="tarjeta">
        <div class="rotulo">Valor del inventario</div>
        <div class="valor"><fmt:formatNumber value="${valorInventario}" type="currency" currencySymbol="$" maxFractionDigits="0"/></div>
        <div class="nota">stock por precio de venta</div>
    </div>
    <div class="tarjeta ${contadorReponer gt 0 ? 'alerta' : ''}">
        <div class="rotulo">Hay que reponer</div>
        <div class="valor">${contadorReponer}</div>
        <div class="nota">${contadorAgotados} sin unidades</div>
    </div>
    <div class="tarjeta ${contadorVencidos gt 0 ? 'alerta' : ''}">
        <div class="rotulo">Lotes a vigilar</div>
        <div class="valor">${contadorPorVencer}</div>
        <div class="nota">${contadorVencidos} ya vencidos</div>
    </div>
    <div class="tarjeta">
        <div class="rotulo">Proveedores</div>
        <div class="valor">${cantidadProveedores}</div>
        <div class="nota">cargados en el sistema</div>
    </div>
</div>

<div class="dos-columnas">
    <div class="bloque">
        <div class="encabezado-bloque">
            <h2>Hay que reponer</h2>
            <a class="btn btn-chico" href="${ctx}/Reponer">Ver todo</a>
        </div>
        <c:choose>
            <c:when test="${empty reponer}">
                <p class="vacio">Ningún producto está por debajo de su stock mínimo.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <caption>Ordenados por urgencia: primero los agotados.</caption>
                    <thead>
                        <tr>
                            <th scope="col">Producto</th>
                            <th scope="col" class="num">Stock</th>
                            <th scope="col">Estado</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="p" items="${reponer}" end="5">
                            <tr class="${p.stock eq 0 ? 'fila-agotada' : 'fila-alerta'}">
                                <td>
                                    ${p.nombre}
                                    <div class="chico">${p.codigo}</div>
                                </td>
                                <td class="num">
                                    ${p.stock} / ${p.stockMinimo}
                                    <%@include file="partes/barra.jsp"%>
                                </td>
                                <td><%@include file="partes/estado.jsp"%></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="bloque">
        <div class="encabezado-bloque">
            <h2>Últimos movimientos</h2>
            <a class="btn btn-chico" href="${ctx}/RegistrarMovimiento">Registrar</a>
        </div>
        <c:choose>
            <c:when test="${empty movimientos}">
                <p class="vacio">Todavía no hay movimientos registrados.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                        <tr>
                            <th scope="col">Fecha</th>
                            <th scope="col">Producto</th>
                            <th scope="col">Tipo</th>
                            <th scope="col" class="num">Cantidad</th>
                            <th scope="col" class="num">Queda</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="m" items="${movimientos}">
                            <tr>
                                <td class="chico"><fmt:formatDate value="${m.fecha}" pattern="dd/MM/yy HH:mm"/></td>
                                <td>${m.productoIdproducto.nombre}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${m.tipo eq 'ENTRADA'}"><span class="badge badge-ok">Entrada</span></c:when>
                                        <c:when test="${m.tipo eq 'SALIDA'}"><span class="badge badge-bajo">Salida</span></c:when>
                                        <c:otherwise><span class="badge badge-neutro">Ajuste</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="num">${m.cantidad}</td>
                                <td class="num">${m.stockResultante}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%@include file="partes/pie.jsp"%>
