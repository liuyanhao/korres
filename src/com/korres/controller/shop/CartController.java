package com.korres.controller.shop;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.korres.entity.Cart;
import com.korres.entity.CartItem;
import com.korres.entity.Member;
import com.korres.entity.Product;
import com.korres.service.CartItemService;
import com.korres.service.CartService;
import com.korres.service.MemberService;
import com.korres.service.ProductService;
import com.korres.util.CookieUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.korres.Message;

@Controller("shopCartController")
@RequestMapping( { "/cart" })
public class CartController extends BaseController {

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "productServiceImpl")
	private ProductService productService;

	@Resource(name = "cartServiceImpl")
	private CartService cartService;

	@Resource(name = "cartItemServiceImpl")
	private CartItemService cartItemService;

	@RequestMapping(value = { "/add" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message add(Long id, Integer quantity, HttpServletRequest request,
			HttpServletResponse response) {
		if ((quantity == null) || (quantity.intValue() < 1)) {
			return SHOP_MESSAGE_ERROR;
		}

		Product product = (Product) this.productService.find(id);
		if (product == null) {
			return Message.warn("shop.cart.productNotExsit", new Object[0]);
		}

		if (!product.getIsMarketable().booleanValue()) {
			return Message
					.warn("shop.cart.productNotMarketable", new Object[0]);
		}

		if (product.getIsGift().booleanValue()) {
			return Message.warn("shop.cart.notForSale", new Object[0]);
		}

		Cart cart = this.cartService.getCurrent();
		Member member = this.memberService.getCurrent();
		if (cart == null) {
			cart = new Cart();
			cart.setKey(UUID.randomUUID().toString()
					+ DigestUtils
							.md5Hex(RandomStringUtils.randomAlphabetic(30)));
			cart.setMember(member);
			this.cartService.save(cart);
		}

		if ((Cart.MAX_PRODUCT_COUNT != null)
				&& (cart.getCartItems().size() >= Cart.MAX_PRODUCT_COUNT
						.intValue())) {
			return Message.warn("shop.cart.addCountNotAllowed",
					new Object[] { Cart.MAX_PRODUCT_COUNT });
		}

		CartItem cartItem = null;
		if (cart.contains(product)) {
			cartItem = cart.getCartItem(product);
			if ((CartItem.MAX_QUANTITY != null)
					&& (cartItem.getQuantity().intValue() + quantity.intValue() > CartItem.MAX_QUANTITY
							.intValue())) {
				return Message.warn("shop.cart.maxCartItemQuantity",
						new Object[] { CartItem.MAX_QUANTITY });
			}

			if ((product.getStock() != null)
					&& (cartItem.getQuantity().intValue() + quantity.intValue() > product
							.getAvailableStock().intValue())) {
				return Message.warn("shop.cart.productLowStock", new Object[0]);
			}

			cartItem.add(quantity.intValue());
			this.cartItemService.update(cartItem);
		} else {
			if ((CartItem.MAX_QUANTITY != null)
					&& (quantity.intValue() > CartItem.MAX_QUANTITY.intValue())) {
				return Message.warn("shop.cart.maxCartItemQuantity",
						new Object[] { CartItem.MAX_QUANTITY });
			}

			if ((product.getStock() != null)
					&& (quantity.intValue() > product.getAvailableStock()
							.intValue())) {
				return Message.warn("shop.cart.productLowStock", new Object[0]);
			}

			cartItem = new CartItem();
			cartItem.setQuantity(quantity);
			cartItem.setProduct(product);
			cartItem.setCart(cart);
			this.cartItemService.save(cartItem);
			cart.getCartItems().add(cartItem);
		}

		if (member == null) {
			CookieUtils.addCookie(request, response, "cartId", cart.getId()
					.toString(), Integer.valueOf(604800));
			CookieUtils.addCookie(request, response, "cartKey", cart.getKey(),
					Integer.valueOf(604800));
		}

		return Message.success("shop.cart.addSuccess", new Object[] {
				Integer.valueOf(cart.getQuantity()),
				setScale(cart.getAmount(), true, false) });
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(ModelMap model) {
		model.addAttribute("cart", this.cartService.getCurrent());
		return "/shop/cart/list";
	}
	
	@RequestMapping(value = { "/quantity" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Map<String, Object> quantity(ModelMap model) {
		Map<String, Object> map = new HashMap<String, Object>();
		Cart cart = this.cartService.getCurrent();
		if ((cart == null) || (cart.isEmpty())) {
			map.put("quantity", Integer.valueOf(0));
		}
		else{
			Set<CartItem> set = cart.getCartItems();
			map.put("quantity", Integer.valueOf(set.size()));
		}
		
		map.put("message", SHOP_MESSAGE_SUCCESS);
		
		return map;
	}

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> edit(Long id, Integer quantity) {
		Map<String, Object> map = new HashMap<String, Object>();
		if ((quantity == null) || (quantity.intValue() < 1)) {
			map.put("message", SHOP_MESSAGE_ERROR);
			return map;
		}

		Cart cart = this.cartService.getCurrent();
		if ((cart == null) || (cart.isEmpty())) {
			map.put("message", Message.error("shop.cart.notEmpty",
					new Object[0]));
			return map;
		}

		CartItem cartItem = this.cartItemService.find(id);
		Set<CartItem> set = cart.getCartItems();
		if ((cartItem == null) || (set == null) || (!set.contains(cartItem))) {
			map.put("message", Message.error("shop.cart.cartItemNotExsit",
					new Object[0]));
			return map;
		}

		if ((CartItem.MAX_QUANTITY != null)
				&& (quantity.intValue() > CartItem.MAX_QUANTITY.intValue())) {
			map.put("message", Message.warn("shop.cart.maxCartItemQuantity",
					new Object[] { CartItem.MAX_QUANTITY }));
			return map;
		}

		Product product = cartItem.getProduct();
		if ((product.getStock() != null)
				&& (quantity.intValue() > product.getAvailableStock()
						.intValue())) {
			map.put("message", Message.warn("shop.cart.productLowStock",
					new Object[0]));
			return map;
		}

		cartItem.setQuantity(quantity);
		this.cartItemService.update(cartItem);
		map.put("message", SHOP_MESSAGE_SUCCESS);
		map.put("subtotal", cartItem.getSubtotal());
		map.put("isLowStock", Boolean.valueOf(cartItem.getIsLowStock()));
		map.put("quantity", Integer.valueOf(cart.getQuantity()));
		map.put("point", Integer.valueOf(cart.getPoint()));
		map.put("amount", cart.getAmount());
		map.put("promotions", cart.getPromotions());
		map.put("giftItems", cart.getGiftItems());

		return map;
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> delete(Long id) {
		Map<String, Object> map = new HashMap<String, Object>();
		Cart cart = this.cartService.getCurrent();
		if ((cart == null) || (cart.isEmpty())) {
			map.put("message", Message.error("shop.cart.notEmpty",
					new Object[0]));
			return map;
		}
		CartItem cartItem = (CartItem) this.cartItemService.find(id);
		Set<CartItem> set = cart.getCartItems();
		if ((cartItem == null) || (set == null) || (!set.contains(cartItem))) {
			map.put("message", Message.error("shop.cart.cartItemNotExsit",
					new Object[0]));
			return map;
		}

		set.remove(cartItem);
		this.cartItemService.delete(cartItem);
		map.put("message", SHOP_MESSAGE_SUCCESS);
		map.put("quantity", Integer.valueOf(cart.getQuantity()));
		map.put("point", Integer.valueOf(cart.getPoint()));
		map.put("amount", cart.getAmount());
		map.put("promotions", cart.getPromotions());
		map.put("isLowStock", Boolean.valueOf(cart.getIsLowStock()));

		return map;
	}

	@RequestMapping(value = { "/clear" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message clear() {
		Cart cart = this.cartService.getCurrent();
		this.cartService.delete(cart);

		return SHOP_MESSAGE_SUCCESS;
	}
}