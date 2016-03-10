package iae.home.utils.view;

import iae.home.money2011.v2.R;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ChartView  extends View {
	private Double m_seriesX[]=null;
	private Double m_seriesY[]=null;
	
	private RectF m_clientRect=null;
	
	private Paint m_paintChart=new Paint();
	private Paint m_paintGrid=new Paint();
	private Paint m_paintText=new Paint();
	
	private int m_backgroundColor=Color.WHITE;
	
	double m_minX=1e6, m_maxX=-1e6;
	double m_minY=1e6, m_maxY=-1e6;

	public ChartView(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray a=context.obtainStyledAttributes(attrs,R.styleable.ChartView);
		
		m_backgroundColor=a.getColor(R.styleable.ChartView_android_colorBackground, Color.WHITE);
		
		m_paintText.setStyle(Paint.Style.STROKE);
		m_paintText.setColor(a.getColor(R.styleable.ChartView_android_textColor, Color.BLUE));
		m_paintText.setTextSize(a.getDimension(R.styleable.ChartView_android_textSize, 10));
		
		m_paintGrid.setStyle(Paint.Style.STROKE);
		m_paintGrid.setColor(a.getColor(R.styleable.ChartView_gridColor, Color.GRAY));
		
		m_paintChart.setStyle(Paint.Style.STROKE);
		m_paintChart.setColor(a.getColor(R.styleable.ChartView_chartColor, Color.RED));
	}

	public void setSeriesX(Double[] values) {
		this.m_minX=1e6; 
		this.m_maxX=-1e6;
		this.m_seriesX=values;

		if(values==null)
			return;
		
		for(int i=0; i<this.m_seriesX.length;i++) {
			this.m_minX=Math.min(this.m_minX, this.m_seriesX[i]);
			this.m_maxX=Math.max(this.m_maxX, this.m_seriesX[i]);
		}
	}

	public void setSeriesY(Double[] values) {
		this.m_minY=0; 
		this.m_maxY=-1e6;
		this.m_seriesY=values;

		if(values==null)
			return;

		for(int i=0; i<this.m_seriesY.length;i++) {
			this.m_minY=Math.min(this.m_minY, this.m_seriesY[i]);
			this.m_maxY=Math.max(this.m_maxY, this.m_seriesY[i]);
		}
	}

	public void setTextColor(int color) {
		m_paintText.setColor(color);
	}

	public void setTextSize(float size) {
		m_paintText.setTextSize(size);
	}
	
	public void setGridColor(int color) {
		m_paintGrid.setColor(color);
	}

	public void setChartColor(int color) {
		m_paintChart.setColor(color);
	}
	
    public class AxesDef
    {
		public Double[] Axes;
    }
	
    private AxesDef GetAxesDef(double start, double end)
    {
            double regionSize = Math.abs(start - end);
            double AxesScale = Math.pow(10, Math.floor(Math.log10(regionSize)));
            double normalizeSize = regionSize / AxesScale;

            switch ((int)normalizeSize)
            {
                case 1:
                    AxesScale *= 0.2;
                    break;
                case 2:
                    AxesScale *= 0.5;
                    break;
                case 3:
                    AxesScale *= 0.5;
                    break;
                case 4:
                    AxesScale *= 1;
                    break;
                case 5:
                    AxesScale *= 1;
                    break;
                case 6:
                    AxesScale *= 1;
                    break;
                case 7:
                    AxesScale *= 2;
                    break;
                case 8:
                    AxesScale *= 2;
                    break;
                case 9:
                    AxesScale *= 2;
                    break;
            }

            int iStart = (int)Math.floor(start / AxesScale) - 1;
            int iEnd = (int)Math.floor(end / AxesScale) + 1;

            
           ArrayList<Double> lst = new ArrayList<Double>();

            for (int i = iStart; i < iEnd; i++)
            {
                double ax = i * AxesScale;
                if ((ax >= start) && (ax <= end))
                    lst.add(ax);
            }

            AxesDef def = new AxesDef();
            def.Axes=new Double[lst.size()];
            lst.toArray(def.Axes);
            return def;
    }
    
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.drawColor(m_backgroundColor);
		
		if(m_seriesX == null || m_seriesX.length <=0) {
			RectF r=new RectF((float)(m_clientRect.left+5),5,
	                 (float)(m_clientRect.right-5),(float)(m_clientRect.bottom-5));
			
			canvas.drawRect(r, m_paintGrid);
			return;
		}
			
		AxesDef adX=GetAxesDef(m_minX, m_maxX);
		AxesDef adY=GetAxesDef(m_minY, m_maxY);

		double leftBarWidth=-1e6;
		
		Rect txtBounds=new Rect();
		
		m_paintText.getTextBounds("1000", 0, 3, txtBounds);
		
		float txtHeight=txtBounds.bottom-txtBounds.top; 
		
		for(int i=0;i<adY.Axes.length;i++) {
			leftBarWidth=Math.max(leftBarWidth, m_paintText.measureText(String.valueOf(adY.Axes[i])));
		}

		RectF graphRec=new RectF((float)(m_clientRect.left+10+leftBarWidth),5,
				                 (float)(m_clientRect.right-5),(float)(m_clientRect.bottom-5-txtHeight-5));
		
		double koefX=(graphRec.right-graphRec.left)/(m_maxX-m_minX);
		double baseX=graphRec.left-m_minX*koefX;
		double koefY=(graphRec.bottom-graphRec.top)/(m_maxY-m_minY);
		double baseY=graphRec.bottom+m_minY*koefY;		
		
		canvas.drawRect(graphRec, m_paintGrid);
		
		for(int i=0;i<adY.Axes.length;i++) {
			float ly=(float)(baseY-adY.Axes[i]*koefY);
			float w=m_paintText.measureText(String.valueOf(adY.Axes[i]));
			
			canvas.drawText(String.valueOf(adY.Axes[i]), 
							graphRec.left-w-5, ly+txtHeight/2, 
							m_paintText);
			
			canvas.drawLine(graphRec.left, ly, 
							graphRec.right, ly, 
					        m_paintGrid);
		}

		for(int i=0;i<adX.Axes.length;i++) {
			float lx=(float)(baseX+adX.Axes[i]*koefX);
			canvas.drawLine(lx, graphRec.top, 
					        lx, graphRec.bottom, 
					        m_paintGrid);
			canvas.drawText(String.valueOf(adX.Axes[i]), 
							lx, m_clientRect.bottom-5, 
							m_paintText);
		}
		
		for(int i=0;i<m_seriesX.length-1;i++)
			canvas.drawLine((float)(baseX+m_seriesX[i]*koefX), (float)(baseY-m_seriesY[i]*koefY), 
							(float)(baseX+m_seriesX[i+1]*koefX), (float)(baseY-m_seriesY[i+1]*koefY), 
							m_paintChart);
	}
	
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    	m_clientRect=new RectF(0,0,r-l,b-t);
    }	
}

