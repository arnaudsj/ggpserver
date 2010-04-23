<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	template style sheet

	use with:
		<xsl:call-template name="template">
			<xsl:with-param name="stateWidth">250</xsl:with-param>
		</xsl:call-template name="template">

	The template assumes that there is a template with name "print_state" that prints the state in the surrounding div.
	print_state is in the context /match/state
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="sitespecific.xsl"/> <!-- $stylesheetURL, makeStepLinkURL and makePlayLinkURL template -->
	
	<xsl:import href="title.xsl"/>
	<xsl:import href="header.xsl"/>
	<xsl:import href="footer.xsl"/>
	<xsl:import href="history.xsl"/>
	<xsl:import href="playerInfo.xsl"/>
	<xsl:import href="playClock.xsl"/>
	<xsl:import href="state.xsl"/>
	
	<xsl:template name="main" match="/">
		
		<html>

			<head>
				<link rel="stylesheet" type="text/css" href="formate.css">
					<xsl:attribute name="href"><xsl:value-of select="$stylesheetURL"/>generic/css/main.css</xsl:attribute>
				</link>
				<xsl:call-template name="title"/>
			</head>

			<body>
				
				<xsl:call-template name="header" />
				
				<table>
					<tr>
						<td style="padding: 10px; vertical-align: top;">
							<xsl:for-each select="match/state">
								<div class="state">
									<xsl:call-template name="print_state"/>
								</div>
							</xsl:for-each>
						</td>
						<td style="padding: 10px; vertical-align: top;">
							<xsl:call-template name="playClock" />
							<!--<br/>-->
							<xsl:call-template name="playerInfo"/>
							<!--<br/>-->
							<xsl:call-template name="history"/>
						</td>
					</tr>
				</table>

				<xsl:call-template name="footer"/>
			</body>
		</html>

	</xsl:template>

</xsl:stylesheet>
