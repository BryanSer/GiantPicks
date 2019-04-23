/*
 * 开发者:Bryan_lzh
 * QQ:390807154
 * 保留一切所有权
 * 若为Bukkit插件 请前往plugin.yml查看剩余协议
 */
package br.giantPicks;

/**
 *
 * @author Bryan_lzh
 * @version 1.0
 * @since 2019-4-22
 */
public class Config<T> {

    private T target;
    private String name;

    public Config(String name, T target) {
        this.target = target;
        this.name = name;
    }

    public T getTarget() {
        return target;
    }

    public void setTarget(T target) {
        this.target = target;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
