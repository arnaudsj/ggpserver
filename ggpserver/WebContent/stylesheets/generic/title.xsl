<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	- Widget for setting page title to match-id.
	- Assumes a tag of the form <match><match-id>...</match-id>.
	- For use within <head>.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template name="title">

		<title>
			<xsl:value-of select="/match/match-id"/>
		</title>

	</xsl:template>
</xsl:stylesheet>
