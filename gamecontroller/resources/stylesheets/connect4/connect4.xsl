<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/chess_board.xsl"/>
	
	<xsl:template name="print_state">
		<xsl:call-template name="print_chess_state">
			<xsl:with-param name="checkered" select="'no'"/>
			<xsl:with-param name="DefaultCellContent">no</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="make_cell_content">
		<xsl:param name="piece"/>
		<xsl:param name="content"/>
		<xsl:param name="background"/>
		<xsl:param name="alt"/>

		<xsl:variable name="piece2">
			<xsl:choose>
				<xsl:when test="$content='DIRT'">xx</xsl:when>
				<xsl:when test="$content='R'">O2</xsl:when>
				<xsl:when test="$content='W'">O1</xsl:when>
				<xsl:otherwise><xsl:value-of select="$piece"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="background2">
			<xsl:choose>
				<xsl:when test="$content='DIRT'">dark</xsl:when>
				<xsl:otherwise><xsl:value-of select="$background"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:call-template name="make_chess_img">
			<xsl:with-param name="piece" select="$piece2"/>
			<xsl:with-param name="background" select="$background2"/>
			<xsl:with-param name="alt" select="$alt"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="linkOnCell">
		<xsl:param name="x" />
		<xsl:param name="y" />
		<xsl:param name="content" />
		<xsl:param name="piece" />
		
		<xsl:variable name="move" select="/match/legalmoves/move[move-term/prop-f='DROP' and move-term/arg[1]=$x]/move-number"/>
		<xsl:if test="$move != '' and count(/match/state/fact[prop-f='CELL' and arg[1]=$x and arg[3] != 'B']) = $y">
			<xsl:text disable-output-escaping="yes">javascript:location.replace("</xsl:text>
			<xsl:call-template name="makePlayLinkURL">
				<xsl:with-param name="chosenMove" select="$move"/>
			</xsl:call-template>
			<xsl:text disable-output-escaping="yes">")</xsl:text>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>