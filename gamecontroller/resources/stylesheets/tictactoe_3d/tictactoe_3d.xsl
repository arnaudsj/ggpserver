<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	TicTacToe 3D
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>

	<xsl:template name="print_state">

		<xsl:variable name="Width">
			<xsl:for-each select="fact[prop-f='CELL']/arg[1]">
				<xsl:sort select="." order="descending"/>
				<xsl:if test="position()=1">
					<xsl:value-of select="."/>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="Height">
			<xsl:for-each select="fact[prop-f='CELL']/arg[2]">
				<xsl:sort select="." order="descending"/>
				<xsl:if test="position()=1">
					<xsl:value-of select="."/>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="Depth">
			<xsl:for-each select="fact[prop-f='CELL']/arg[3]">
				<xsl:sort select="." order="descending"/>
				<xsl:if test="position()=1">
					<xsl:value-of select="."/>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>

		<style type="text/css" media="all">
			div.tictactoe_3d_board{
				position: relative;
				width:    <xsl:value-of select="($Width - 1) * 52 + ($Height - 1) * 25 + 78"/>px;
				height:   <xsl:value-of select="($Depth - 1) * ($Height + 1) * 25 + ($Height - 1) * 25 + 42"/>px;
				padding: 0px;
			}
			div.tictactoe_3d_cell{
				position:absolute;
				height: 21px;
				width:39px;
				background-color: transparent;
			}
		</style>

		<div class="tictactoe_3d_board">
			<xsl:for-each select="/match/state/fact[prop-f='CELL']">
				<xsl:sort select="arg[3]" order="descending"/> <!-- z  -->
				<xsl:sort select="arg[1]" order="descending"/> <!-- y  -->
				<xsl:sort select="arg[2]" order="ascending"/> <!-- x  -->

				<xsl:variable name="alt"><xsl:call-template name="fluent2text"/></xsl:variable>
				
				<xsl:variable name="zCoord" select="arg[3]"/>
				<xsl:variable name="yCoord" select="arg[1]"/>
				<xsl:variable name="xCoord" select="arg[2]"/>
				<xsl:variable name="content" select="arg[4]"/>

				<xsl:variable name="yCell" select="($Depth - $zCoord) * ($Height + 1) * 25 + ($Height - $yCoord) * 25"/>
				<xsl:variable name="xCell" select="($xCoord - 1) * 52 + ($yCoord - 1) * 25"/>


				<div class="tictactoe_3d_cell">
					<xsl:attribute name="style">
						left: <xsl:value-of select="$xCell"/>px;
						top:  <xsl:value-of select="$yCell"/>px;
					</xsl:attribute>

					<img width="78px" height="42px">
						<xsl:attribute name="src">
							<xsl:value-of select="$stylesheetURL"/>
							<xsl:text>/tictactoe_3d/cell_</xsl:text>
							<xsl:choose>
								<xsl:when test="$content='X'">
									<xsl:text>blue</xsl:text>
								</xsl:when>
								<xsl:when test="$content='O'">
									<xsl:text>red</xsl:text>
								</xsl:when>
								<xsl:when test="$content='B'">
									<xsl:choose>
										<xsl:when test="
											../fact[prop-f='SELECTED']
											and
											(not (../fact[prop-f='SELECTED' and arg[1]='ROW']) or (../fact[prop-f='SELECTED' and arg[1]='ROW']/arg[2]=$yCoord))
											and
											(not (../fact[prop-f='SELECTED' and arg[1]='COLUMN']) or (../fact[prop-f='SELECTED' and arg[1]='COLUMN']/arg[2]=$xCoord))
											and
											(not (../fact[prop-f='SELECTED' and arg[1]='LEVEL']) or (../fact[prop-f='SELECTED' and arg[1]='LEVEL']/arg[2]=$zCoord))
											">
											<xsl:text>possible</xsl:text>
										</xsl:when>
										<xsl:otherwise>
											<xsl:text>blank</xsl:text>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:when>
							</xsl:choose>
							<xsl:text>.png</xsl:text>
						</xsl:attribute>
						<xsl:attribute name="alt"><xsl:value-of select="$alt"/></xsl:attribute>
						<xsl:attribute name="title"><xsl:value-of select="$alt"/></xsl:attribute>
					</img>
				</div>

			</xsl:for-each>
		</div>
	</xsl:template>
</xsl:stylesheet>
