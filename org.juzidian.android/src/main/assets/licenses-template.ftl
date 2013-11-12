<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=utf-8"/>
		<link rel="stylesheet" type="text/css" href="licenses.css"/>
		<title>Juidian Licensing</title>
	</head>
	<body>

		<h1>About Juzidian Licensing</h1>

		<p>Juzidian is distributed under the GNU General Public License version
		3.</p>

		<p>Juzidian also contains third-party free software libraries which are
		distributed under various other licenses.</p>

		<p>The full license text for each of the applicable licenses are listed
		below.</p>

		<#list licenses as license>
			<h2>${license.title}</h2>
			<p>
				${license.html}
			</p>
		</#list>

	</body>
</html>
