package com.vise.xsnow.permission;

/**
 * @Description: 权限实体类
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2017-04-19 20:14
 */
public class Permission {
    //权限名称
    public final String name;
    //是否授予权限
    public final boolean granted;
    //是否显示请求权限理由
    public final boolean shouldShowRequestPermissionRationale;

    public Permission(String name, boolean granted) {
        this(name, granted, false);
    }

    public Permission(String name, boolean granted, boolean shouldShowRequestPermissionRationale) {
        this.name = name;
        this.granted = granted;
        this.shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale;
    }

    @Override
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Permission that = (Permission) o;

        if (granted != that.granted) return false;
        if (shouldShowRequestPermissionRationale != that.shouldShowRequestPermissionRationale)
            return false;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (granted ? 1 : 0);
        result = 31 * result + (shouldShowRequestPermissionRationale ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "name='" + name + '\'' +
                ", granted=" + granted +
                ", shouldShowRequestPermissionRationale=" + shouldShowRequestPermissionRationale +
                '}';
    }
}
