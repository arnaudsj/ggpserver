<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	- Widget for writing state to the screen.
	- assumes to be in the context /match/state
	- For use within <body>
	- needs css/main.css
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template name="state">
		<xsl:param name="excludeFluent"/>
		<xsl:param name="excludeFluent2"/>
		<xsl:param name="excludeFluent3"/>
		<xsl:param name="excludeFluent4"/>
		<xsl:param name="excludeFluent5"/>
		<xsl:param name="excludeFluent6"/>
		<xsl:param name="excludeFluent7"/>
		<xsl:param name="excludeFluent8"/>
		<xsl:param name="excludeFluent9"/>
		<xsl:param name="excludeFluent10"/>

		<span class="heading">
			<xsl:if test="$excludeFluent!=''">Remaining </xsl:if>
			State:
		</span>
		<div class="underline"/>

		<table>
			<xsl:for-each select="fact[prop-f!=$excludeFluent and prop-f!=$excludeFluent2 and prop-f!=$excludeFluent3 and prop-f!=$excludeFluent4 and prop-f!=$excludeFluent5 and prop-f!=$excludeFluent6 and prop-f!=$excludeFluent7 and prop-f!=$excludeFluent8 and prop-f!=$excludeFluent9 and prop-f!=$excludeFluent10]">
				<xsl:sort select="."/>

				<tr>
					<td>
						<span class="heading">(<xsl:value-of select="./prop-f"/></span>
						<span class="content">
							<xsl:for-each select="./arg">
								<xsl:text> </xsl:text>
								<xsl:value-of select="."/>
							</xsl:for-each>
						</span>
						<span class="heading">)</span>
					</td>
				</tr>
			</xsl:for-each>
		</table>

	</xsl:template>

</xsl:stylesheet>
