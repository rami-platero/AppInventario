<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<c:set var="titulo" value="Vencimientos"/>
<%@include file="partes/cabecera.jsp"%>

<h1>Vencimientos</h1>
<p class="bajada">
    Lotes vencidos o que vencen en ${diasAviso} días o menos, ordenados por urgencia.
    El vencimiento pertenece al lote y no al producto: un mismo artículo puede tener varias
    tandas con fechas distintas.
</p>

<div class="tarjetas">
    <div class="tarjeta ${contadorVencidos gt 0 ? 'alerta' : ''}">
        <div class="rotulo">Lotes vencidos</div>
        <div class="valor">${contadorVencidos}</div>
        <div class="nota">no se pueden vender</div>
    </div>
    <div class="tarjeta">
        <div class="rotulo">Lotes a vigilar</div>
        <div class="valor">${contadorPorVencer}</div>
        <div class="nota">vencidos o por vencer</div>
    </div>
    <div class="tarjeta">
        <div class="rotulo">Unidades en juego</div>
        <div class="valor">${unidadesPorVencer}</div>
        <div class="nota">en esos lotes</div>
    </div>
</div>

<p class="nota-metodo">
    <strong>Cómo se calcula la sugerencia.</strong>
    Se mide el ritmo de venta observado de cada producto: las unidades que salieron divididas
    por los días de historial que el sistema realmente tiene, no una ventana fija. Con eso se
    proyecta cuántas unidades se venderían antes del vencimiento y cuántas sobrarían.
    El porcentaje de descuento es una heurística sobre cuánto hay que acelerar la venta:
    el sistema no tiene datos de elasticidad precio-demanda y no finge tenerlos. Si hay menos
    de 3 salidas o menos de 7 días de historial, no proyecta nada y lo dice.
</p>

<c:choose>
    <c:when test="${empty sugerencias}">
        <div class="bloque">
            <p class="vacio">
                Ningún lote está vencido ni vence en los próximos ${diasAviso} días.
            </p>
        </div>
    </c:when>
    <c:otherwise>
        <div class="sugerencias">
            <c:forEach var="s" items="${sugerencias}">
                <c:set var="p" value="${s.lote.productoIdproducto}"/>
                <div class="sugerencia nivel-${s.nivel}">
                    <div class="cabeza">
                        <div>
                            <h3>${s.titulo}</h3>
                            <div class="producto">
                                ${p.nombre}
                                <c:if test="${not empty s.lote.codigo}">&middot; lote ${s.lote.codigo}</c:if>
                            </div>
                        </div>
                        <c:choose>
                            <c:when test="${s.estado eq 'VENCIDO'}"><span class="badge badge-agotado">${s.etiquetaEstado}</span></c:when>
                            <c:when test="${s.estado eq 'CRITICO'}"><span class="badge badge-agotado">${s.etiquetaEstado}</span></c:when>
                            <c:otherwise><span class="badge badge-bajo">${s.etiquetaEstado}</span></c:otherwise>
                        </c:choose>
                    </div>

                    <c:if test="${not empty s.descuento}">
                        <div class="descuento">${s.descuento}% de descuento sugerido</div>
                    </c:if>

                    <p class="detalle">${s.detalle}</p>

                    <dl>
                        <dt>Vencimiento</dt>
                        <dd>
                            <fmt:formatDate value="${s.lote.vencimiento}" pattern="dd/MM/yyyy"/>
                            (${s.descripcionDias})
                        </dd>
                        <dt>Cantidad en el lote</dt>
                        <dd>${s.lote.cantidad} ${p.unidad}</dd>
                        <c:if test="${not empty s.ritmo.porDia}">
                            <dt>Ritmo observado</dt>
                            <dd><fmt:formatNumber value="${s.ritmo.porDia}" maxFractionDigits="2"/> ${p.unidad} por día</dd>
                        </c:if>
                        <c:if test="${not empty s.proyeccion}">
                            <dt>Se venderían</dt>
                            <dd>${s.proyeccion} ${p.unidad} antes de vencer</dd>
                        </c:if>
                        <c:if test="${not empty s.unidadesEnRiesgo and s.unidadesEnRiesgo gt 0}">
                            <dt>En riesgo</dt>
                            <dd>
                                ${s.unidadesEnRiesgo} ${p.unidad}
                                (<fmt:formatNumber value="${s.valorEnRiesgo}" type="currency" currencySymbol="$" maxFractionDigits="0"/>)
                            </dd>
                        </c:if>
                        <dt>Historial</dt>
                        <dd>${s.ritmo.salidas} salidas en ${s.ritmo.diasHistorial} días</dd>
                        <dt>Confianza</dt>
                        <dd>${s.ritmo.etiquetaConfianza}</dd>
                    </dl>

                    <p class="fundamento">
                        <strong>En qué se basa:</strong> ${s.fundamento}
                    </p>

                    <div class="pie">
                        <a class="btn btn-chico" href="${ctx}/RegistrarMovimiento?producto=${p.idproducto}&amp;tipo=SALIDA&amp;lote=${s.lote.idlote}">Registrar salida</a>
                        <form action="${ctx}/DescartarLote" method="post"
                              onsubmit="return confirm('¿Dar de baja las ${s.lote.cantidad} ${p.unidad} de este lote? Se registra como merma.');">
                            <input type="hidden" name="id" value="${s.lote.idlote}">
                            <button class="btn btn-chico btn-peligro" type="submit">Dar de baja</button>
                        </form>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<%@include file="partes/pie.jsp"%>
