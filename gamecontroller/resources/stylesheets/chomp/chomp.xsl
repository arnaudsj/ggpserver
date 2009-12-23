<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/chess_board.xsl"/>
	
	<xsl:template name="print_state">
		<xsl:call-template name="print_chess_state">
			<xsl:with-param name="Width">8</xsl:with-param>
			<xsl:with-param name="Height">7</xsl:with-param>
			<xsl:with-param name="DefaultCellContent">no</xsl:with-param>
			<xsl:with-param name="checkered">invisible</xsl:with-param>
			<xsl:with-param name="CellFluentName">CHOCOLATE</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="make_cell_content">
		<xsl:param name="xArg"/>
		<xsl:param name="yArg"/>
		<xsl:param name="alt"/>

		<xsl:variable name="piece">
			<xsl:choose>
				<xsl:when test="$xArg='1' and $yArg='1'">j2</xsl:when>
				<xsl:otherwise/>
			</xsl:choose>
		</xsl:variable>

		<xsl:call-template name="make_chess_img">
			<xsl:with-param name="piece" select="$piece"/>
			<xsl:with-param name="background">dark</xsl:with-param>
			<xsl:with-param name="alt"><xsl:value-of select="$alt"/></xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
</xsl:stylesheet>