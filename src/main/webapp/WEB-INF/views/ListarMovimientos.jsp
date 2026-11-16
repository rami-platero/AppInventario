<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>
<c:set var="titulo" value="Movimientos"/>
<%@include file="partes/cabecera.jsp"%>

<div class="encabezado-bloque">
    <div>
        <h1>Movimientos</h1>
        <p class="bajada">
            El historial no se edita ni se borra. Para corregir un error se registra un ajuste.
        </p>
    </div>
    <a class="btn btn-primario" href="${ctx}/RegistrarMovimiento">Registrar movimiento</a>
</div>

<form class="filtros" method="get" action="${ctx}/ListarMovimientos">
    <div>
        <label for="producto">Producto</label>
        <select id="producto" name="producto">
            <option value="">Todos</option>
            <c:forEach var="p" items="${productos}">
                <option value="${p.idproducto}"
                        ${param.producto eq p.idproducto ? 'selected' : ''}>${p.nombre}</option>
            </c:forEach>
        </select>
    </div>
    <div>
        <label for="tipo">Tipo</label>
        <select id="tipo" name="tipo">
            <option value="" ${empty param.tipo ? 'selected' : ''}>Todos</option>
            <option value="ENTRADA" ${param.tipo eq 'ENTRADA' ? 'selected' : ''}>Entradas</option>
            <option value="SALIDA" ${param.tipo eq 'SALIDA' ? 'selected' : ''}>Salidas</option>
            <option value="AJUSTE" ${param.tipo eq 'AJUSTE' ? 'selected' : ''}>Ajustes</option>
        </select>
    </div>
    <button class="btn" type="submit">Filtrar</button>
    <a class="btn" href="${ctx}/ListarMovimientos">Limpiar</a>
</form>

<div class="bloque">
    <c:choose>
        <c:when test="${empty movimientos}">
            <p class="vacio">No hay movimientos que coincidan con el filtro.</p>
        </c:when>
        <c:otherwise>
            <table>
                <caption>${fn:length(movimientos)} movimientos, del más nuevo al más viejo.</caption>
                <thead>
                    <tr>
                        <th scope="col">Fecha</th>
                        <th scope="col">Producto</th>
                        <th scope="col">Tipo</th>
                        <th scope="col" class="num">Cantidad</th>
                        <th scope="col" class="num">Stock resultante</th>
                        <th scope="col">Proveedor</th>
                        <th scope="col">Motivo</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="m" items="${movimientos}">
                        <tr>
                            <td class="chico"><fmt:formatDate value="${m.fecha}" pattern="dd/MM/yyyy HH:mm"/></td>
                            <td>
                                ${m.productoIdproducto.nombre}
                                <div class="chico">${m.productoIdproducto.codigo}</div>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${m.tipo eq 'ENTRADA'}"><span class="badge badge-ok">Entrada</span></c:when>
                                    <c:when test="${m.tipo eq 'SALIDA'}"><span class="badge badge-bajo">Salida</span></c:when>
                                    <c:otherwise><span class="badge badge-neutro">Ajuste</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="num">${m.cantidad} ${m.productoIdproducto.unidad}</td>
                            <td class="num">${m.stockResultante}</td>
                            <td class="chico">
                                <c:choose>
                                    <c:when test="${empty m.proveedorIdproveedor}">&mdash;</c:when>
                                    <c:otherwise>${m.proveedorIdproveedor.nombre}</c:otherwise>
                                </c:choose>
                            </td>
                            <td class="chico">${m.motivo}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>

<%@include file="partes/pie.jsp"%>
