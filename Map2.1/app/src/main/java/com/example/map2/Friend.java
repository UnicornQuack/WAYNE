package com.example.map2;

class Friend {
    private String name;
    private double latitude;
    private double longitude;
    private int iconResId;  // 圖標資源 ID
    private String statusMessage; // 狀態訊息
    private long lastOnline;      // 最後上線時間（時間戳）

    public Friend(String name, double latitude, double longitude, int iconResId, String statusMessage, long lastOnline) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.iconResId = iconResId;
        this.statusMessage = statusMessage;
        this.lastOnline = lastOnline;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public long getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(long lastOnline) {
        this.lastOnline = lastOnline;
    }
}
