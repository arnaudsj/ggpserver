<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/chess_board.xsl"/>
	
	
	<xsl:template name="legalMove">
		<xsl:param name="url"/> <!-- the URL to propose this move (no confirm) -->
		<xsl:param name="urlWithConfirm"/> <!-- the URL to propose this move (with confirm of this move) -->
		
		<xsl:if test="./move-term/prop-f = 'MARK'">
			<script language="JavaScript" type="text/javascript">
				<xsl:text disable-output-escaping="yes">address[</xsl:text>
				<xsl:value-of select="./move-term/arg[1]"/> <!-- x-coordinate -->
				<xsl:text disable-output-escaping="yes">][</xsl:text>
				<xsl:value-of select="./move-term/arg[2]"/> <!-- y-coordinate -->
				<xsl:text disable-output-escaping="yes">] = "</xsl:text>
				<xsl:value-of select="$urlWithConfirm" />
				<xsl:text disable-output-escaping="yes">";</xsl:text>
			</script>
		</xsl:if>
		
	</xsl:template>
	
	
	<xsl:template name="linkOnCell">
		<xsl:param name="x" />
		<xsl:param name="y" />
		<xsl:param name="content" />
		<xsl:param name="piece" />
		<xsl:choose>
			<xsl:when test="$content='B'">1</xsl:when>
			<xsl:otherwise>0</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	
	<xsl:template name="print_state">
		
		<script language="JavaScript" type="text/javascript">
			<![CDATA[
				<!--
					var address = new Array(4);
					for (i = 0; i < 4; i++) {
						address[i] = new Array(4);
					}
				-->
			]]>
		</script>
		
		<script language="JavaScript" type="text/javascript">
			<![CDATA[
				<!--
				function callBack (x, y, content, piece) {
					//alert(address);
					if (address[x][y] != undefined && address[x][y] != "") {
						location.replace(address[x][y]);
					}
				}
				-->
			]]>
		</script>
		
		<xsl:call-template name="print_chess_state">
			<xsl:with-param name="cellCallBack">
				<xsl:if test="count(/match/legalmoves) = 1">
					callBack
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>