#ifdef GL_ES
precision mediump float;
#endif

// Posted by Trisomie21

uniform float time;
uniform vec2 mouse;
uniform float R;
uniform float G;
uniform float B;
uniform int X;
uniform int Y;
uniform vec2 resolution;

// from http://glsl.heroku.com/e#5248.0
#define BLADES 2.0
#define BIAS 0.1
#define SHARPNESS 3.0

float rand(vec2 co){ return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453); }

vec3 star(vec2 position) {
	float blade = clamp(pow(sin(atan(position.y,position.x )*BLADES)+BIAS, SHARPNESS), 0.0, 1.0);
	vec3 color = mix(vec3(-0.5, -0.5, -0.5), vec3(-0.2, -0.2, -0.2), (position.y + 1.0) * 0.25);
	color += (vec3(0.4, 0.4, 0.4) * 1.0 / distance(vec2(0.0), position) * 0.075);
	color += vec3(0.1, 0.1, 0.1) * min(0.1, blade *0.4) * (1.0 / distance(vec2(0.0, 0.0), position)*0.075);
	return color;
}


// Tweaked from http://glsl.heroku.com/e#4982.0
float hash( float n ) { return fract(sin(n)*43758.5453); }

float noise( in vec2 x )
{
	vec2 p = floor(x);
	vec2 f = fract(x);
    	f = f*f*(3.0-2.0*f);
    	float n = p.x + p.y*57.0;
    	float res = mix(mix(hash(n+0.0), hash(n+1.0),f.x), mix(hash(n+57.0), hash(n+58.0),f.x),f.y);
    	return res;
}

vec3 cloud(vec2 p) {
	float f = 0.0;
    	f += 0.20000*noise(p*1.0*10.0); 
    	f += 0.25000*noise(p*2.0*10.0); 
    	f += 0.12500*noise(p*4.0*10.0); 
    	f += 0.06250*noise(p*8.0*10.0);	
	f *= f;
	return vec3(f*R*4.0, f*G*4.0, f*B*4.0)*.6;
}

const float LAYERS	= 7.0;
const float SPEED	= 0.003;
const float SCALE	= 50.0;
const float DENSITY	= 1.0;
const float BRIGHTNESS	= 100.0;
       vec2 ORIGIN	= resolution.xy*.5;


void main( void ) {
	vec2  mouse2 = vec2(X,Y);
	vec2   pos = gl_FragCoord.xy - mouse2;
	float dist = length(pos) / resolution.y;
	vec2 coord = vec2(pow(dist, 0.1), atan(pos.x, pos.y) / (3.1415926*2.0));
	
	// Nebulous cloud
	vec3 color = cloud(pos/resolution);
	
	// Background stars
	float a = pow((1.0-dist),20.0);
	float t = time*-.05;
	float r = coord.x - (t*SPEED);
	float c = fract(a+coord.y + 0.0*.543);
	vec2  p = vec2(r, c*.5)*4000.0;
	vec2 uv = fract(p)*2.0-1.0;
	float m = clamp((rand(floor(p))-.9)*BRIGHTNESS, 0.0, 1.0);
	color +=  clamp((1.0-length(uv*2.0))*m*dist, 0.0, 1.0);
	
	// Flying stars into black hole
	
	// /original loop manually unrolled)
	
	for ( float i = 1.0;i<8.0;i++) {
		a = pow((1.0-dist),2000.0);
		t = i*10.0 - time*i*i;
		r = coord.x + (t*SPEED);
		c = fract(a+coord.y + i*.543);
		p = vec2(r, c*.99)*SCALE*(LAYERS/(i*i));
		uv = fract(p)*2.0-1.0;
		m = clamp((rand(floor(p))-DENSITY/i)*BRIGHTNESS, 0.0, 1.0);
		color +=  clamp(star(uv*0.5)*m*dist, 0.0, 1.0);
	}
	
	gl_FragColor = vec4(color, 1.0);
}