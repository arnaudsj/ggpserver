<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/state.xsl"/>
	
	<xsl:template name="print_state">
		<xsl:call-template name="print_hanoi_board"/>
		
		<xsl:call-template name="state">
			<xsl:with-param name="excludeFluent" select="'ON'"/>
			<xsl:with-param name="excludeFluent2" select="'CLEAR'"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="print_hanoi_board">
		<xsl:variable name="nbOfDiscs" select="count(fact[prop-f='ON'])"/>
		<style type="text/css" media="all">
			table.hanoiboard{
				height: <xsl:value-of select="$nbOfDiscs * 20 + 25"/>px;
				padding: 0px;
			}
			table.hanoiboard td.stack{
				vertical-align: bottom;
				text-align: center;
			}
			table.hanoiboard td.stack div.disc{
				text-align: center;
				color: white;
				line-height: 20px;
				background: maroon;
				height:20px;
				border:0px;
				margin-top: 1px;
				margin-bottom: 1px;
				margin-left:auto;
				margin-right:auto;
			}
			table.hanoiboard td.stack div.pillar{
				width:<xsl:value-of select="$nbOfDiscs * 20 + 20"/>px;
				background: grey;
				border:0px;
				margin-top: 1px;
				margin-bottom: 1px;
				margin-left:auto;
				margin-right:auto;
			}
		</style>

		<table class="hanoiboard">
			<tr>
				<td class="stack" align="center">
					<xsl:call-template name="makestack">
						<xsl:with-param name="on" select="'PILLAR1'"/>
					</xsl:call-template>
				</td>
				<td class="stack" align="center">
					<xsl:call-template name="makestack">
						<xsl:with-param name="on" select="'PILLAR2'"/>
					</xsl:call-template>
				</td>
				<td class="stack" align="center">
					<xsl:call-template name="makestack">
						<xsl:with-param name="on" select="'PILLAR3'"/>
					</xsl:call-template>
				</td>
			</tr>
		</table>
	
	</xsl:template>

	<xsl:template name="makestack">
		<xsl:param name="on"/>
		<xsl:variable name="disc" select="fact[prop-f='ON' and arg[2]=$on]/arg[1]"/>
		<xsl:if test="$disc!=''">
			<xsl:call-template name="makestack">
				<xsl:with-param name="on" select="$disc"/>
			</xsl:call-template>
		</xsl:if>
		
		<xsl:call-template name="makedisc">
			<xsl:with-param name="disc" select="$on"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="makedisc">
		<xsl:param name="disc"/>
		<xsl:choose>
			<xsl:when test="substring($disc,1,6)='PILLAR'">
				<div class="pillar"><xsl:value-of select="substring-after($disc,'PILLAR')"/></div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="discNumber" select="substring-after($disc,'DISC')"/>
					<div class="disc">
						<xsl:attribute name="style">
							width: <xsl:value-of select="number($discNumber)*20+10"/>px;
						</xsl:attribute>
						<xsl:value-of select="$discNumber"/>
					</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
