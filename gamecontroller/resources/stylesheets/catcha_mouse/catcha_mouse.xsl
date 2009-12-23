<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/chess_board.xsl"/>
	<xsl:import href="../generic/state.xsl"/>
	
	<xsl:template name="print_state">
		<xsl:variable name="CellWidth" select="'46'"/>
		<xsl:variable name="CellHeight" select="'46'"/>

		<div>
			<xsl:attribute name="style">
				position: relative;
				width:<xsl:value-of select="$CellWidth * 7"/>px;
				height:<xsl:value-of select="$CellHeight * 9"/>px;
			</xsl:attribute>
		 
			<xsl:for-each select="fact[prop-f='CELL']">
				<xsl:variable name="alt"><xsl:call-template name="fluent2text"/></xsl:variable>
				<xsl:variable name="xArg" select="./arg[1]"/>
				<xsl:variable name="yArg" select="./arg[2]"/>
				<xsl:variable name="content" select="./arg[3]"/>
				<xsl:variable name="xArgNumber">
					<xsl:call-template name="coord2number">
						<xsl:with-param name="coord" select="$xArg"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="yArgNumber">
					<xsl:call-template name="coord2number">
						<xsl:with-param name="coord" select="$yArg"/>
					</xsl:call-template>
				</xsl:variable>
				
				<xsl:variable name="xPosCell" select="$CellWidth * ($xArgNumber - 1)"/>
<!--					<xsl:choose>
						<xsl:when test="translate($yArg, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', '')=''"><xsl:value-of select="$CellWidth * $xArgNumber - ($CellWidth div 2)"/></xsl:when>
						<xsl:otherwise><xsl:value-of select="$CellWidth * ($xArgNumber - 1)"/></xsl:otherwise>
					</xsl:choose>
				</xsl:variable>-->
				<xsl:variable name="yPosCell">
					<xsl:choose>
						<xsl:when test="$yArg='H'"><xsl:value-of select="0"/></xsl:when>
						<xsl:otherwise><xsl:value-of select="$CellHeight * (9 - $yArgNumber)"/></xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="divHeight">
					<xsl:choose>
						<xsl:when test="$yArg='H'"><xsl:value-of select="$CellHeight * 2 - 2"/></xsl:when>
						<xsl:otherwise><xsl:value-of select="$CellHeight - 2"/></xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:text>
				</xsl:text>
				<div>
					<xsl:attribute name="title"><xsl:value-of select="$alt"/></xsl:attribute>
					<xsl:attribute name="style">
						position: absolute;
						width: <xsl:value-of select="$CellWidth - 2"/>px;
						height: <xsl:value-of select="$divHeight"/>px;
						left: <xsl:value-of select="$xPosCell"/>px;
						top: <xsl:value-of select="$yPosCell"/>px;
						background-color: #D18B47; <!-- this is the same color as the dark background of the chess images -->
					</xsl:attribute>
					
					<xsl:variable name="piece">
						<xsl:choose>
							<xsl:when test="$content='MOUSE'">hl</xsl:when>
							<xsl:when test="$content='HOLE'">xo</xsl:when>
							<xsl:when test="$content='TRAP'">xx</xsl:when>
							<xsl:when test="$content='EMPTY'"/>
						</xsl:choose>
					</xsl:variable>
					
					<xsl:if test="$yArg='H' and $content!='EMPTY'">
						<div> <!-- spacer to move the cell content to the middle -->
							<xsl:attribute name="style">
								width: <xsl:value-of select="$CellWidth - 2"/>px;
								height: <xsl:value-of select="($CellHeight - 2) div 2"/>px;
							</xsl:attribute>
						</div>
					</xsl:if>
					<xsl:call-template name="make_chess_img">
						<xsl:with-param name="piece" select="$piece"/>
						<xsl:with-param name="background" select="'dark'"/>
						<xsl:with-param name="alt" select="$alt"/>
					</xsl:call-template>
				</div>
			</xsl:for-each>
		</div>
		<!-- show remaining fluents -->
		<xsl:call-template name="state">
			<xsl:with-param name="excludeFluent" select="'CELL'"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>