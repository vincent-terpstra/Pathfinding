# Pathfinding

<img src="ScreenShot2019-09-27.png">

'''html
<html>
       <head>
              <title>PathHex</title>
              <meta http-equiv="content-type" content="text/html; charset=UTF-8">
              <meta id="gameViewport" name="viewport" content="width=device-width initial-scale=1">
              <link href="styles.css" rel="stylesheet" type="text/css">
              <script src="soundmanager2-setup.js"></script>
  			  <script src="soundmanager2-jsmin.js"></script>
       </head>

       <body>
              <a class="superdev" href="javascript:%7B%20window.__gwt_bookmarklet_params%20%3D%20%7B'server_url'%3A'http%3A%2F%2Flocalhost%3A9876%2F'%7D%3B%20var%20s%20%3D%20document.createElement('script')%3B%20s.src%20%3D%20'http%3A%2F%2Flocalhost%3A9876%2Fdev_mode_on.js'%3B%20void(document.getElementsByTagName('head')%5B0%5D.appendChild(s))%3B%7D">&#8635;</a>
              <div align="center" id="embed-html"></div>
              <script type="text/javascript" src="html/html.nocache.js"></script>
       </body>

       <script>
              document.getElementById('gameViewport').setAttribute('content',
                 'width=device-width initial-scale=' + 1/window.devicePixelRatio);
              function handleMouseDown(evt) {
                evt.preventDefault();
                evt.stopPropagation();
                evt.target.style.cursor = 'default';
                window.focus();
              }
              function handleMouseUp(evt) {
                evt.preventDefault();
                evt.stopPropagation();
                evt.target.style.cursor = '';
              }
              document.getElementById('embed-html').addEventListener('mousedown', handleMouseDown, false);
              document.getElementById('embed-html').addEventListener('mouseup', handleMouseUp, false);
       </script>
</html>
'''
