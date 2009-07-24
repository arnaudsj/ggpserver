<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	- Widget for drawing the footer (disclaimer, etc.)
	- For use within <body>.
	- needs css/main.css and sitespecific.xsl
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template name="footer">
		<div class="footer">
			<div class="underline"/>
			
			<span class="heading">Webmaster:</span>
			<span class="content">
				<xsl:call-template name="webmaster"/>
			</span>
			<br/>
			This visualization is part of <a href="http://ggpserver.sourceforge.net/">GGP Server</a>.
			<br/>
			Design partially provided by the <a href="http://logic.stanford.edu/">Stanford Logic Group</a>.
			<br/>
			
		</div>
	</xsl:template>


</xsl:stylesheet>
