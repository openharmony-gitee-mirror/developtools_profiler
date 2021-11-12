/*
 * Copyright (c) 2021 Huawei Device Co., Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ohos.devtools.views.distributed.component;

import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import ohos.devtools.views.distributed.util.DistributedCommon;
import ohos.devtools.views.trace.AbstractNode;
import ohos.devtools.views.trace.AbstractRow;
import ohos.devtools.views.trace.Tip;
import ohos.devtools.views.trace.util.Utils;

import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static java.util.Objects.nonNull;

/**
 * TraceFuncRow
 *
 * @since 2021/5/18 13:56
 */
public class TraceFuncRow<R extends AbstractNode> extends AbstractRow {
    private final Integer tid;
    private IRender render;
    private List<R> data;
    private R currentData;
    private Supplier<List<R>> supplier;
    private Long rangeStartNS;
    private Long rangeEndNS;
    private Integer x1;
    private Integer x2;

    /**
     * structure
     *
     * @param name name
     * @param tid tid
     */
    public TraceFuncRow(String name, Integer tid) {
        super(name, true, false);
        this.tid = tid;
        layout.setComponentConstraints(expandBtn, "split 2,w 5!,gapleft 15,gaptop 3,gapbottom push");
        layout.setComponentConstraints(nameLabel, "gapleft 5,gaptop 3,gapbottom push,w 70!,align left");
        loadData();
        nameLabelClickConsumer = (event) -> {
            setSelect(true, null, null);
        };
    }

    /**
     * getTid
     *
     * @return Integer tid
     */
    public Integer getTid() {
        return tid;
    }

    /**
     * setRender
     *
     * @param render set current render
     */
    public void setRender(IRender<R> render) {
        this.render = render;
    }

    /**
     * set thread function Supplier
     *
     * @param supplier set thread function supplier
     */
    public void setSupplier(Supplier<List<R>> supplier) {
        this.supplier = supplier;
    }

    @Override
    public void contentPaint(Graphics graphics) {
        if (render != null && graphics instanceof Graphics2D) {
            Graphics2D g2 = (Graphics2D) graphics;
            if (data != null) {
                render.paint(g2, data);
            } else {
                g2.setColor(JBColor.foreground());
                g2.drawString("Loading...", 10, 10 + 12);
                loadData();
            }
            if (nonNull(x1) && nonNull(x2)) {
                int xMin = Math.min(x1, x2);
                int xMax = Math.max(x1, x2);
                int width = Math.abs(x2 - x1);
                DistributedCommon.setAlpha(g2, 0.5F);
                g2.setColor(JBColor.foreground().darker());
                g2.fillRect(0, 0, xMin, getHeight());
                g2.fillRect(xMax, 0, getWidth() - xMax, getHeight());
                DistributedCommon.setAlpha(g2, 1F);
            }
        }
    }

    @Override
    public void mouseMoveHandler(Point point) {
        super.mouseMoveHandler(point);
        if (nonNull(data)) {
            if (data.stream().filter(it -> contains(it)).anyMatch(it -> it.getRect().contains(point))) {
                data.stream().filter(it -> contains(it) && it.getRect().contains(point)).forEach(it -> {
                    List<String> stringList = it.getStringList(getTimeByX(Utils.getX(point)));
                    Tip.getInstance().display(content, point, stringList);
                    if (Objects.nonNull(currentData)) {
                        currentData.moveOut(point, content);
                    }
                    it.moveIn(point, content);
                    currentData = it;
                });
                return;
            } else {
                if (Objects.nonNull(currentData)) {
                    currentData.moveOut(point, content);
                }
                Tip.getInstance().display(content, point, Arrays.asList(getTimeByX(Utils.getX(point))));
            }
        }
    }

    @Override
    public void loadData() {
        if (!isLoading.get()) {
            isLoading.set(true);
            CompletableFuture.runAsync(() -> {
                if (nonNull(supplier)) {
                    data = supplier.get();
                }
                SwingUtilities.invokeLater(() -> {
                    isLoading.set(false);
                    content.repaint();
                });
            }, Utils.getPool()).whenComplete((unused, throwable) -> {
                if (Objects.nonNull(throwable)) {
                    throwable.printStackTrace();
                }
            });
        }
    }

    /**
     * setSelect
     *
     * @param flag flag
     * @param x1 x1
     * @param x2 x2
     */
    public void setSelect(boolean flag, Integer x1, Integer x2) {
        this.x1 = x1;
        this.x2 = x2;
        if (flag) {
            if (nonNull(x1) && nonNull(x2)) {
                rangeStartNS = x2ns(Math.min(x1, x2));
                rangeEndNS = x2ns(Math.max(x1, x2));
                DistributedTracePanel.setRangeStartNS(rangeStartNS);
                DistributedTracePanel.setRangeEndNS(rangeEndNS);
            }
            setBorder(new LineBorder(JBUI.CurrentTheme.Link.linkColor(), 1));
            setBackground(JBUI.CurrentTheme.Link.linkSecondaryColor());
        } else {
            rangeStartNS = null;
            rangeEndNS = null;
            setBorder(null);
            setBackground(JBColor.background());
        }
    }

    @Override
    public void refreshNotify() {
        super.refreshNotify();
        if (nonNull(rangeStartNS) && nonNull(rangeEndNS)) {
            x1 = (int) DistributedCommon.ns2x(rangeStartNS, getContentBounds(), DistributedTracePanel.getDURATION());
            x2 = (int) DistributedCommon.ns2x(rangeEndNS, getContentBounds(), DistributedTracePanel.getDURATION());
        }
    }

    private long x2ns(int x1) {
        long start = Math.min(startNS, endNS);
        long end = Math.max(startNS, endNS);
        long dur = Math.abs(endNS - startNS);
        if (x1 >= getContentBounds().width) {
            return end;
        }
        if (x1 <= 0) {
            return start;
        }
        double ns = x1 * (dur) * 1.0 / getContentBounds().width;
        return (long) ns + start;
    }

    /**
     * Gets the value of data2 .
     *
     * @return the value of java.util.List<R>
     */
    public List<R> getData() {
        return data;
    }

    /**
     * interface IRender
     */
    public interface IRender<R> {
        /**
         * paint
         *
         * @param graphics graphics
         * @param data data
         */
        void paint(Graphics2D graphics, List<R> data);
    }
}
