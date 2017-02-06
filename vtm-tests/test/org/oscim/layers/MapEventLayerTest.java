package org.oscim.layers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.oscim.event.Gesture;
import org.oscim.event.MotionEvent;
import org.oscim.map.Animator;
import org.oscim.map.Map;
import org.oscim.map.ViewController;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MapEventLayerTest {
    private MapEventLayer layer;
    private ViewController mockViewport;
    private Animator mockAnimator;
    private ArgumentCaptor<Float> argumentCaptor;

    @Before
    public void setUp() throws Exception {
        Map mockMap = Mockito.mock(Map.class);
        mockViewport = Mockito.mock(ViewController.class);
        mockAnimator = Mockito.mock(Animator.class);
        layer = new MapEventLayer(mockMap);
        when(mockMap.viewport()).thenReturn(mockViewport);
        when(mockMap.animator()).thenReturn(mockAnimator);
        when(mockMap.getHeight()).thenReturn(6);
        argumentCaptor = ArgumentCaptor.forClass(float.class);
    }

    @Test
    public void shouldNotBeNull() throws Exception {
        assertThat(layer).isNotNull();
    }

    @Test
    public void doubleTap_shouldAnimateZoom() throws Exception {
        simulateDoubleTap();
        verify(mockAnimator).animateZoom(300, 2, 1.0f, -2.0f);
    }

    @Test
    public void doubleTap_shouldAnimateZoomAfterDoubleTouchDrag() throws Exception {
        simulateDoubleTouchDragUp();
        simulateDoubleTap();
        verify(mockAnimator).animateZoom(300, 2, 1.0f, -2.0f);
    }

    @Test
    public void doubleTouchDrag_shouldNotAnimateZoom() throws Exception {
        simulateDoubleTouchDragUp();
        verify(mockAnimator, never()).animateZoom(any(long.class), any(double.class),
                any(float.class), any(float.class));
    }

    @Test
    public void doubleTouchDragUp_shouldDecreaseContentScale() throws Exception {
        simulateDoubleTouchDragUp();
        verify(mockViewport).scaleMap(argumentCaptor.capture(), any(float.class), any(float.class));
        assertThat(argumentCaptor.getValue()).isLessThan(1);
    }

    @Test
    public void doubleTouchDragDown_shouldIncreaseContentScale() throws Exception {
        simulateDoubleTouchDragDown();
        verify(mockViewport).scaleMap(argumentCaptor.capture(), any(float.class), any(float.class));
        assertThat(argumentCaptor.getValue()).isGreaterThan(1);
    }

    private void simulateDoubleTap() {
        layer.onTouchEvent(new TestMotionEvent(MotionEvent.ACTION_DOWN, 1, 1));
        layer.onGesture(Gesture.DOUBLE_TAP, new TestMotionEvent(MotionEvent.ACTION_UP, 1, 1));
        layer.onTouchEvent(new TestMotionEvent(MotionEvent.ACTION_UP, 1, 1));
    }

    private void simulateDoubleTouchDragUp() {
        layer.onTouchEvent(new TestMotionEvent(MotionEvent.ACTION_DOWN, 1, 1));
        layer.onGesture(Gesture.DOUBLE_TAP, new TestMotionEvent(MotionEvent.ACTION_MOVE, 1, 0));
        layer.onTouchEvent(new TestMotionEvent(MotionEvent.ACTION_MOVE, -100, 0));
        layer.onTouchEvent(new TestMotionEvent(MotionEvent.ACTION_UP, 1, 0));
    }

    private void simulateDoubleTouchDragDown() {
        layer.onTouchEvent(new TestMotionEvent(MotionEvent.ACTION_DOWN, 1, 1));
        layer.onGesture(Gesture.DOUBLE_TAP, new TestMotionEvent(MotionEvent.ACTION_MOVE, 1, 2));
        layer.onTouchEvent(new TestMotionEvent(MotionEvent.ACTION_MOVE, 100, 2));
        layer.onTouchEvent(new TestMotionEvent(MotionEvent.ACTION_UP, 1, 2));
    }

    private class TestMotionEvent extends MotionEvent {
        final int action;
        final float x;
        final float y;

        TestMotionEvent(int action, float x, float y) {
            this.action = action;
            this.x = x;
            this.y = y;
        }

        @Override
        public long getTime() {
            return 0;
        }

        @Override
        public int getAction() {
            return action;
        }

        @Override
        public float getX() {
            return x;
        }

        @Override
        public float getY() {
            return y;
        }

        @Override
        public float getX(int idx) {
            return x;
        }

        @Override
        public float getY(int idx) {
            return y;
        }

        @Override
        public int getPointerCount() {
            return 0;
        }

        @Override
        public MotionEvent copy() {
            return new TestMotionEvent(action, x, y);
        }

        @Override
        public void recycle() {
        }
    }
}
