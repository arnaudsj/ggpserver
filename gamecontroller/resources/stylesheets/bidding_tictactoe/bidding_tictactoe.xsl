<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	Bidding-TicTacToe
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>

	<xsl:template name="print_state">

		<style type="text/css" media="all">
			div.board
			{
				width:    150px;
				height:   150px;
				font: bold 40px fixed;
			}
			div.coins
			{
				width:    150px;
			}
			div.cell
			{
				width:  46px;
				height: 46px;
				float:	left;
				border: 2px solid #777777;
				background-color: #EEEEEE;
				text-align: center;
				<!-- vertical-align: middle;-->
				<!--font: bold 30px arial;-->
			}
		</style>

		<div class="board">
			<xsl:for-each select="fact[prop-f='CELL']">
				<xsl:sort select="arg[2]" order="ascending"/>
				<xsl:sort select="arg[1]"/>
				<div class="cell">
					<!--<p style="text-align: center;">-->
					<xsl:choose>
						<xsl:when test="arg[3]='X'">
							X
						</xsl:when>
						<xsl:when test="arg[3]='O'">
							O
						</xsl:when>
					</xsl:choose>
					<!--</p>-->
				</div>
			</xsl:for-each>
		</div>

		<div class="coins">
			<strong>Coins:</strong>
			<table border="1">
				<tr><th style="width:50px; background-color: #CCCCCC;">X</th><th style="width:50px; background-color: #CCCCCC;">O</th></tr>
				<tr>
					<xsl:for-each select="fact[prop-f='COINS']">
						<xsl:sort select="arg[1]" order="descending"/>
						<xsl:variable name="who" select="arg[1]"/>
						<td style="text-align:center;">
							<xsl:value-of select="arg[2]"/>
							<xsl:if test="../fact[prop-f='TIEBREAKER']/arg[1]=$who">
								*
							</xsl:if>
						</td>
					</xsl:for-each>
				</tr>
			</table>
		</div>

	</xsl:template>

</xsl:stylesheet>