<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<c:set var="titulo" value="${empty producto ? 'Agregar producto' : 'Editar producto'}"/>
<%@include file="partes/cabecera.jsp"%>

<h1>${titulo}</h1>
<p class="bajada">
    <c:choose>
        <c:when test="${empty producto}">
            El stock inicial se puede cargar acá. Después, todo cambio de stock pasa por Movimientos.
        </c:when>
        <c:otherwise>
            El stock no se edita a mano: se cambia desde Movimientos, para que quede historial.
        </c:otherwise>
    </c:choose>
</p>

<p class="leyenda">Los campos con <span class="req">*</span> son obligatorios.</p>
<div class="bloque">
    <form class="campos" method="post"
          action="${ctx}/${empty producto ? 'AgregarProducto' : 'EditarProducto'}">
        <c:if test="${not empty producto}">
            <input type="hidden" name="id" value="${producto.idproducto}">
        </c:if>

        <div>
            <label for="codigo">Código <span class="req" title="Campo obligatorio">*</span></label>
            <input type="text" id="codigo" name="codigo" value="${producto.codigo}"
                   maxlength="20" required>
            <div class="ayuda">Identificador interno, único. Por ejemplo ALM-001.</div>
        </div>

        <div>
            <label for="nombre">Nombre <span class="req" title="Campo obligatorio">*</span></label>
            <input type="text" id="nombre" name="nombre" value="${producto.nombre}"
                   maxlength="60" required>
        </div>

        <div>
            <label for="categoria">Categoría <span class="req" title="Campo obligatorio">*</span></label>
            <select id="categoria" name="categoria" required>
                <c:forEach var="cat" items="${categorias}">
                    <option value="${cat.idcategoria}"
                            ${producto.categoriaIdcategoria.idcategoria eq cat.idcategoria ? 'selected' : ''}>${cat.nombre}</option>
                </c:forEach>
            </select>
        </div>

        <div>
            <label for="unidad">Unidad <span class="req" title="Campo obligatorio">*</span></label>
            <select id="unidad" name="unidad" required>
                <c:forEach var="u" items="${unidades}">
                    <option value="${u}" ${producto.unidad eq u ? 'selected' : ''}>${u}</option>
                </c:forEach>
            </select>
        </div>

        <div>
            <label for="perecedero">¿Vence?</label>
            <c:choose>
                <c:when test="${empty producto}">
                    <label class="chico" style="font-weight:400">
                        <input type="checkbox" id="perecedero" name="perecedero" value="1"
                               style="width:auto" onchange="document.getElementById('stock').disabled = this.checked;">
                        Es un producto perecedero
                    </label>
                    <div class="ayuda">
                        Si vence, el stock se lleva por lotes: cada entrada pide su fecha de
                        vencimiento y el stock del producto es la suma de sus lotes.
                    </div>
                </c:when>
                <c:otherwise>
                    <input type="text" id="perecedero"
                           value="${producto.perecedero ? 'Sí, lleva lotes' : 'No vence'}" disabled>
                    <div class="ayuda">
                        No se puede cambiar después de crear el producto: habría que reconstruir
                        los lotes desde el historial.
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <div>
            <label for="stock">Stock actual</label>
            <c:choose>
                <c:when test="${empty producto}">
                    <input type="number" id="stock" name="stock" value="0" min="0" step="1" required>
                    <div class="ayuda">
                        Unidades con las que arranca el producto. Si es perecedero queda en cero:
                        las unidades entran con su lote desde Movimientos.
                    </div>
                </c:when>
                <c:otherwise>
                    <input type="number" id="stock" value="${producto.stock}" disabled>
                    <div class="ayuda">
                        Bloqueado a propósito.
                        <a href="${ctx}/RegistrarMovimiento?producto=${producto.idproducto}">Registrar un movimiento</a>.
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <div>
            <label for="stockMinimo">Stock mínimo <span class="req" title="Campo obligatorio">*</span></label>
            <input type="number" id="stockMinimo" name="stockMinimo"
                   value="${empty producto ? 0 : producto.stockMinimo}" min="0" step="1" required>
            <div class="ayuda">Por debajo de este valor el producto aparece en "Hay que reponer".</div>
        </div>

        <div>
            <label for="precio">Precio de venta <span class="req" title="Campo obligatorio">*</span></label>
            <input type="number" id="precio" name="precio"
                   value="${empty producto ? '0.00' : producto.precio}" min="0" step="0.01" required>
        </div>

        <div class="campo-ancho">
            <label for="descripcion">Descripción</label>
            <input type="text" id="descripcion" name="descripcion" value="${producto.descripcion}"
                   maxlength="200">
        </div>

        <div class="pie-form">
            <button class="btn btn-primario" type="submit">Guardar</button>
            <a class="btn" href="${ctx}/ListarProductos">Cancelar</a>
        </div>
    </form>
</div>

<%@include file="partes/pie.jsp"%>
