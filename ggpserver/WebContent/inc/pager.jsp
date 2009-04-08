<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

	<c:if test="${pager.numberOfPages != 1}">
	<br />
		<c:forEach var="i" begin="1" end="${pager.numberOfPages}">
			<c:choose>
				<c:when test="${i == pager.page}" >
					<b>${i}</b>
				</c:when>
				<c:otherwise>
					<c:url value="${pager.targetJsp}" var="pageURL">
						<c:param name="page" value="${i}" />
					</c:url>
					<a href='<c:out value="${pageURL}" />'>${i}</a>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</c:if>
