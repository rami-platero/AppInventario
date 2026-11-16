<c:choose>
    <c:when test="${p.stock eq 0}"><span class="badge badge-agotado">Agotado</span></c:when>
    <c:when test="${p.stock le p.stockMinimo}"><span class="badge badge-bajo">Stock bajo</span></c:when>
    <c:otherwise><span class="badge badge-ok">Normal</span></c:otherwise>
</c:choose>
