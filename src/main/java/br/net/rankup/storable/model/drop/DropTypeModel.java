package br.net.rankup.storable.model.drop;

import org.bukkit.inventory.*;

public class DropTypeModel
{
    private final String name;
    private final String item;
    private final double price;
    private final ItemStack icon;
    
    DropTypeModel(final String name, final String item, final double price, final ItemStack icon) {
        this.name = name;
        this.price = price;
        this.icon = icon;
        this.item = item;
    }
    
    public static DropTypeModelBuilder builder() {
        return new DropTypeModelBuilder();
    }
    
    public String getName() {
        return this.name;
    }

    public String getItem() {
        return this.item;
    }
    
    public double getPrice() {
        return this.price;
    }
    
    public ItemStack getIcon() {
        return this.icon;
    }
    
    public static class DropTypeModelBuilder
    {
        private String name;
        private String item;
        private double price;
        private ItemStack icon;
        
        DropTypeModelBuilder() {
        }
        
        public DropTypeModelBuilder name(final String name) {
            this.name = name;
            return this;
        }

        public DropTypeModelBuilder item(final String item) {
            this.item = item;
            return this;
        }
        
        public DropTypeModelBuilder price(final double price) {
            this.price = price;
            return this;
        }
        
        public DropTypeModelBuilder icon(final ItemStack icon) {
            this.icon = icon;
            return this;
        }
        
        public DropTypeModel build() {
            return new DropTypeModel(this.name, this.item, this.price, this.icon);
        }
        
        @Override
        public String toString() {
            return "DropTypeModel.DropTypeModelBuilder(name=" + this.name + ", price=" + this.price + ", icon=" + this.icon + ")";
        }
    }
}
