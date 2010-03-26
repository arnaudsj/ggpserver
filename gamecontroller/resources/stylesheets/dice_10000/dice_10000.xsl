<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/state.xsl"/>
	<xsl:import href="../generic/dice.xsl"/>
	
	<xsl:template name="print_state">
		<xsl:for-each select="fact[prop-f='DICE']">
			<xsl:for-each select="arg">
				<xsl:variable name="value" select="."/>
				<xsl:variable name="index" select=":position()"/>
				<xsl:variable name="color">
					<xsl:choose>
						<xsl:when test="../../fact[prop-f='KEEPING_DICE']/arg[$index]='T'">black</xsl:when>
						<xsl:when test="../../fact[prop-f='should_roll']">rolling</xsl:when>
						<xsl:otherwise>red</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:choose>
					<xsl:when test="$color='rolling'">
						<div>
							<xsl:attribute name="style">
								width:56px;
								height:56px;
								background-image:
									url(<xsl:call-template name="make_dice_url">
											<xsl:with-param name="value" select="'blank'"/>
											<xsl:with-param name="color" select="'red'"/>
										</xsl:call-template>);
								padding: 10px;
							</xsl:attribute>
							<span class="heading">?</span>
						</div>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="print_dice">
							<xsl:with-param name="value" select="$value"/>
							<xsl:with-param name="color" select="$color"/>
							<xsl:with-param name="width" select="'56'"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			<xsl:for-each>
		</xsl:for-each>
		<br/>
		<span class="heading">current points:</span><xsl:value-of select="fact[prop-f='CURRENT_POINTS']/arg[1]"/><br/>
		<span class="heading">secured points:</span>
		<table>
			<tr>
				<xsl:for-each select="fact[prop-f='POINTS']">
					<th><xsl:value-of select="arg[1]"/></th>
				</xsl:for-each>
			</tr>
			<tr>
				<xsl:for-each select="fact[prop-f='POINTS']">
					<td><xsl:value-of select="arg[2]"/></td>
				</xsl:for-each>
			</tr>
		</table>

		<xsl:call-template name="state">
			<xsl:with-param name="excludeFluent" select="'CONTROL'"/>
			<xsl:with-param name="excludeFluent2" select="'CURRENT_POINTS'"/>
			<xsl:with-param name="excludeFluent3" select="'POINTS'"/>
			<xsl:with-param name="excludeFluent3" select="'DICE'"/>
			<xsl:with-param name="excludeFluent3" select="'KEEPING_DICE'"/>
		</xsl:call-template>

	</xsl:template>
	

</xsl:stylesheet>











