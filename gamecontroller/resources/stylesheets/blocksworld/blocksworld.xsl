<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	Blocksworld
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/state.xsl"/>

	<xsl:template name="print_state">
		
		<style type="text/css" media="all">
			div.fullBlock
			{
				width:  46px;
				height: 46px;
				border: 2px solid #FFC;
				background-color: #CCCCCC;
				padding: 0px;
			}
		</style>

		<table>
			<xsl:attribute name="style">
				border-bottom: 2px solid #CCCCCC;
				height: <xsl:value-of select="count(fact[prop-f='CLEAR' or prop-f='ON'])*52"/>px;
				width: <xsl:value-of select="count(fact[prop-f='CLEAR' or prop-f='ON'])*52"/>px;
			</xsl:attribute>
			<tr>
				<xsl:for-each select="fact[prop-f='TABLE']">
					<xsl:sort select="arg[1]" order="ascending"/>
					<td style="vertical-align: bottom; padding: 0px;">
						<xsl:call-template name="make_stack">
							<xsl:with-param name="block" select="arg[1]"/>
						</xsl:call-template>
					</td>
				</xsl:for-each>
			</tr>
		</table>
		
		<!-- show remaining fluents -->
		<xsl:call-template name="state">
			<xsl:with-param name="excludeFluent" select="'CLEAR'"/>
			<xsl:with-param name="excludeFluent2" select="'TABLE'"/>
			<xsl:with-param name="excludeFluent3" select="'ON'"/>
		</xsl:call-template>
		
	</xsl:template>

	<xsl:template name="make_stack">
		<xsl:param name="block"/>
		
		<xsl:for-each select="../fact[prop-f='ON' and arg[2]=$block]">
			<xsl:call-template name="make_stack">
				<xsl:with-param name="block" select="arg[1]"/>
			</xsl:call-template>
		</xsl:for-each>
		
		<div class="fullBlock">
			<p style="vertical-align: middle; text-align: center; font-weight: bold;">
				<xsl:value-of select="$block"/>
			</p>
		</div>
	</xsl:template>

</xsl:stylesheet>
