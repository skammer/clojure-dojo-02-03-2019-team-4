function drawPolygon(ctx,centerX,centerY,sideCount,size,strokeWidth,strokeColor,fillColor,radians){
    ctx.translate(centerX,centerY);
    ctx.rotate(radians);
    ctx.beginPath();
    ctx.moveTo (size * Math.cos(0), size * Math.sin(0));          
    for (var i = 1; i <= sideCount;i += 1) {
        ctx.lineTo (size * Math.cos(i * 2 * Math.PI / sideCount), size * Math.sin(i * 2 * Math.PI / sideCount));
    }
    ctx.closePath();
    ctx.fillStyle=fillColor;
    ctx.strokeStyle = strokeColor;
    ctx.lineWidth = strokeWidth;
    ctx.stroke();
    ctx.fill();
    ctx.rotate(-radians);
    ctx.translate(-centerX,-centerY);
}
