# Pathfinding

<img src="ScreenShot2019-09-27.png">

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

