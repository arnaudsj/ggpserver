<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/chess_board.xsl"/>
	
	<xsl:template name="print_state">
		<xsl:call-template name="print_chess_state">
			<xsl:with-param name="checkered">alldark</xsl:with-param>
			<xsl:with-param name="DefaultCell">no</xsl:with-param>
			<xsl:with-param name="DefaultCellContent">no</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="make_cell">
		<xsl:param name="col"/>
		<xsl:param name="row"/>
		<xsl:param name="defaultClass"/>
		<div>
			<xsl:attribute name="class">
				<xsl:choose>
					<xsl:when test="fact[prop-f='CELL' and arg[1]=$col and arg[2]=$row and number(arg[3])>0]">cellLight</xsl:when>
					<xsl:otherwise><xsl:value-of select="$defaultClass"/></xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
		</div>
	</xsl:template>
	
	<xsl:template name="make_cell_content">
		<xsl:param name="xArg"/>
		<xsl:param name="yArg"/>
		<xsl:param name="content"/>
		<xsl:param name="piece"/>
		<xsl:param name="background"/>
		<xsl:param name="alt"/>

		<xsl:choose>
			<xsl:when test="$content='0'"/>
			<xsl:otherwise>
				<xsl:call-template name="make_chess_img">
					<xsl:with-param name="piece">x<xsl:value-of select="$content"/></xsl:with-param>
					<xsl:with-param name="background">light</xsl:with-param>
					<xsl:with-param name="imgWidth">22</xsl:with-param>
					<xsl:with-param name="imgHeight">22</xsl:with-param>
					<xsl:with-param name="alt" select="$alt"/>
					<xsl:with-param name="style">
						position: absolute;
						left: <xsl:value-of select="(number($content) mod 2)*22"/>px;
						top: <xsl:value-of select="floor((4 - number($content)) div 2)*22"/>px;
					</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>
	
</xsl:stylesheet>