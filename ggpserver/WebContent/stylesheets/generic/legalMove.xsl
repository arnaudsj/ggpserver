<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	- widget to display a legalMove, this is the default
	- assumes to be in the context /match/legalmoves/move
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template name="legalMove">
		<xsl:param name="url"/> <!-- the URL to propose this move (no confirm) -->
		<xsl:param name="urlWithConfirm"/> <!-- the URL to propose this move (with confirm of this move) -->
		
		<tr>
			<td>
				<span class="content">
				<a>
					<xsl:attribute name="href">
						javascript:location.replace("<xsl:value-of select="$url" />");
					</xsl:attribute>
					<xsl:value-of select="./move-term" />
				</a>
				</span>
			</td>
		</tr>
	</xsl:template>
	
</xsl:stylesheet>
