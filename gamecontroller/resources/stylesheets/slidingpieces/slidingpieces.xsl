<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/chess_board.xsl"/>

	<xsl:template name="print_state">
		<xsl:call-template name="print_chess_state">
			<xsl:with-param name="xArgIdx" select="'2'"/>
			<xsl:with-param name="yArgIdx" select="'1'"/>
			<xsl:with-param name="mirrorY" select="'yes'"/>
			<xsl:with-param name="checkered" select="'no'"/>
			<xsl:with-param name="DefaultCellContent" select="'no'"/>
			<xsl:with-param name="BorderWidth" select="0"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="make_cell_content">
		<xsl:param name="content"/>
		<xsl:param name="alt"/>
		<xsl:if test="$content!='BLANK'">
			<div>
				<xsl:attribute name="style">
					background:
					<xsl:choose>
						<xsl:when test="$content='PIECE1'">red</xsl:when>
						<xsl:when test="$content='PIECE2'">green</xsl:when>
						<xsl:when test="$content='PIECE3'">blue</xsl:when>
						<xsl:when test="$content='PIECE4'">yellow</xsl:when>
						<xsl:when test="$content='PIECE5'">orange</xsl:when>
						<xsl:when test="$content='PIECE6'">magenta</xsl:when>
						<xsl:when test="$content='PIECE7'">cyan</xsl:when>
						<xsl:when test="$content='PIECE8'">brown</xsl:when>
						<xsl:when test="$content='PIECE9'">teal</xsl:when>
						<xsl:otherwise>grey</xsl:otherwise>
					</xsl:choose>;
					width:100%;
					height:100%;
					text-align: center;
					line-height: 44px;
					vertical-align:middle;
				</xsl:attribute>
				<xsl:attribute name="title"><xsl:value-of select="$alt"/></xsl:attribute>
				<xsl:if test="substring($content,1,5)='PIECE'">
					<xsl:value-of select="substring-after($content, 'PIECE')"/>
				</xsl:if>
			</div>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
