<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	Blocksworld
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/state.xsl"/>

	<xsl:template name="print_state">
		
		<xsl:call-template name="print_blocksworlds"/>
		
		<!-- show remaining fluents -->
		<xsl:call-template name="state">
			<xsl:with-param name="excludePattern" select="'CLEAR'"/>
			<xsl:with-param name="excludePattern2" select="'TABLE'"/>
			<xsl:with-param name="excludePattern3" select="'ON'"/>
		</xsl:call-template>
		
	</xsl:template>

	<xsl:template name="print_blocksworlds">
		<xsl:for-each select="fact">
			<xsl:sort select="prop-f"/>
			<!--<xsl:value-of select="prop-f"/> - <xsl:value-of select="preceding-sibling::fact[1]/prop-f"/><br/>-->
			<xsl:if test="not(prop-f = preceding-sibling::fact[1]/prop-f)">
				<xsl:if test="starts-with(prop-f, 'CLEAR')">
					<xsl:variable name="tableID" select="substring-after(prop-f, 'CLEAR')"/>
					<xsl:choose>
						<xsl:when test="../fact[prop-f=concat('TABLE',$tableID)]">
							<xsl:for-each select="..">
								<xsl:call-template name="print_blocksworld">
									<xsl:with-param name="tableID" select="$tableID"/>
								</xsl:call-template>
							</xsl:for-each>
						</xsl:when>
						<xsl:when test="../fact[prop-f=concat('ON',$tableID) and contains(arg[2], 'TABLE')]">
							<xsl:for-each select="..">
								<xsl:call-template name="print_blocksworld">
									<xsl:with-param name="tableID" select="$tableID"/>
									<xsl:with-param name="onFluentTopArgIdx" select="'1'"/>
								</xsl:call-template>
							</xsl:for-each>
						</xsl:when>
						<xsl:when test="../fact[prop-f=concat('ON',$tableID) and contains(arg[1], 'TABLE')]">
							<xsl:for-each select="..">
								<xsl:call-template name="print_blocksworld">
									<xsl:with-param name="tableID" select="$tableID"/>
									<xsl:with-param name="onFluentTopArgIdx" select="'2'"/>
								</xsl:call-template>
							</xsl:for-each>
						</xsl:when>
					</xsl:choose>
				</xsl:if>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="print_blocksworld">
		<xsl:param name="tableID"/>
		<xsl:param name="onFluent" select="concat('ON',$tableID)"/>
		<xsl:param name="onFluentTopArgIdx" select="'1'"/>
		<xsl:param name="clearFluent" select="concat('CLEAR',$tableID)"/>
		<xsl:param name="tableFluent" select="concat('TABLE',$tableID)"/>
		
		<style type="text/css" media="all">
			table.blocksWorld {
				padding: 0px;
				margin: 0px;
				border-collapse: collapse;
				border-bottom: 2px solid #000000;
			}
			table.blocksWorld tr {
				margin: 0px;
				padding: 0px;
				border-style: none;
			}
			table.blocksWorld td {
				margin: 0px;
				vertical-align: bottom;
				padding: 0px 1px 0px 1px;
				width: 50px;
				border-style: none;
			}
			table.blocksWorld td.spacer {
				margin-left: 0px;
				margin-right: auto;
				margin-bottom: 0px;
				vertical-align: bottom;
				padding: 0px;
				width: auto;
				border-style: none;
			}
			table.blocksWorld div.fullBlock {
				width:  46px;
				height: 46px;
				border: 2px solid #888888;
				background-color: #CCCCCC;
				padding: 0px;
				margin: 0px;
			}
			div.fullBlock p {
				vertical-align: middle;
				text-align: center;
				font-weight: bold;
			}
		</style>
		
		<xsl:variable name="numberOfBlocks" select="count(fact[prop-f=$tableFluent or prop-f=$onFluent])"/>

		<table class="blocksWorld">
			<xsl:attribute name="style">
				height: <xsl:value-of select="$numberOfBlocks*50+6"/>px;
				width: <xsl:value-of select="$numberOfBlocks*52+4"/>px;
			</xsl:attribute>
			<caption align="bottom">
				Table <xsl:value-of select="$tableID"/>
				<xsl:comment>
					<xsl:value-of select="$onFluent"/>, <xsl:value-of select="$tableFluent"/>, <xsl:value-of select="$clearFluent"/>
				</xsl:comment>
			</caption>
			<tr>
				<xsl:for-each select="fact[prop-f=$tableFluent or (prop-f=$onFluent and contains(./arg[3 - number($onFluentTopArgIdx)], 'TABLE'))]">
					<xsl:sort select="arg[number($onFluentTopArgIdx)]" order="ascending"/>
					<td>
						<xsl:call-template name="make_stack">
							<xsl:with-param name="block" select="arg[number($onFluentTopArgIdx)]"/>
							<xsl:with-param name="onFluent" select="$onFluent"/>
							<xsl:with-param name="onFluentTopArgIdx" select="number($onFluentTopArgIdx)"/>
						</xsl:call-template>
					</td>
				</xsl:for-each>
				<td class="spacer"></td>
			</tr>
		</table>
		
	</xsl:template>

	<xsl:template name="make_stack">
		<xsl:param name="block"/>
		<xsl:param name="onFluent"/>
		<xsl:param name="onFluentTopArgIdx"/>
		
		<xsl:for-each select="../fact[prop-f=$onFluent and arg[3 - $onFluentTopArgIdx]=$block]">
			<xsl:call-template name="make_stack">
				<xsl:with-param name="block" select="arg[$onFluentTopArgIdx]"/>
				<xsl:with-param name="onFluent" select="$onFluent"/>
				<xsl:with-param name="onFluentTopArgIdx" select="number($onFluentTopArgIdx)"/>
			</xsl:call-template>
		</xsl:for-each>
		
		<div class="fullBlock">
			<p><xsl:value-of select="$block"/></p>
		</div>
	</xsl:template>

</xsl:stylesheet>
