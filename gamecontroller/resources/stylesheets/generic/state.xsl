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
		<xsl:param name="excludePattern" select="';'"/>
		<xsl:param name="excludePattern2" select="';'"/>
		<xsl:param name="excludePattern3" select="';'"/>
		<xsl:param name="excludePattern4" select="';'"/>
		<xsl:param name="excludePattern5" select="';'"/>
		<xsl:param name="excludePattern6" select="';'"/>
		<xsl:param name="excludePattern7" select="';'"/>
		<xsl:param name="excludePattern8" select="';'"/>
		<xsl:param name="excludePattern9" select="';'"/>
		<xsl:param name="excludePattern10" select="';'"/>

		<span class="heading">
			<xsl:if test="$excludeFluent!='' or $excludePattern!=';'">Remaining </xsl:if>
			State:
		</span>
		<div class="underline"/>

		<table>
			<xsl:for-each select="fact[
				prop-f!=$excludeFluent and
				prop-f!=$excludeFluent2 and
				prop-f!=$excludeFluent3 and
				prop-f!=$excludeFluent4 and
				prop-f!=$excludeFluent5 and
				prop-f!=$excludeFluent6 and
				prop-f!=$excludeFluent7 and
				prop-f!=$excludeFluent8 and
				prop-f!=$excludeFluent9 and
				prop-f!=$excludeFluent10 and
				not(contains(prop-f, $excludePattern)) and
				not(contains(prop-f, $excludePattern2)) and
				not(contains(prop-f, $excludePattern3)) and
				not(contains(prop-f, $excludePattern4)) and
				not(contains(prop-f, $excludePattern5)) and
				not(contains(prop-f, $excludePattern6)) and
				not(contains(prop-f, $excludePattern7)) and
				not(contains(prop-f, $excludePattern8)) and
				not(contains(prop-f, $excludePattern9)) and
				not(contains(prop-f, $excludePattern10))
				]">
				<xsl:sort select="."/>

				<tr>
					<td>
						<span class="heading">(<xsl:value-of select="prop-f"/></span>
						<span class="content">
							<xsl:for-each select="arg">
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

	<xsl:template name="fluent2text">(<xsl:value-of select="prop-f"/><xsl:for-each select="arg">&#160;<xsl:value-of select="."/></xsl:for-each>)</xsl:template>

</xsl:stylesheet>
