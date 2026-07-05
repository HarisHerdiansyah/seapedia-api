package com.github.harisherdiansyah.seapediaapi.features.carts;

import com.github.harisherdiansyah.seapediaapi.core.exception.NotFoundException;
import com.github.harisherdiansyah.seapediaapi.core.exception.UnauthorizedException;
import com.github.harisherdiansyah.seapediaapi.core.utils.SecurityUtil;
import com.github.harisherdiansyah.seapediaapi.features.products.ProductEntity;
import com.github.harisherdiansyah.seapediaapi.features.products.ProductRepository;
import com.github.harisherdiansyah.seapediaapi.features.stores.StoreEntity;
import com.github.harisherdiansyah.seapediaapi.features.stores.StoreRepository;
import com.github.harisherdiansyah.seapediaapi.features.users.UserEntity;
import com.github.harisherdiansyah.seapediaapi.features.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;

    public GetCartResponseDTO getAllUserCartItems() {
        UUID userId = SecurityUtil.getCurrentUserId();
        CartEntity cartEntity = cartRepository.findCartEntityByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));
        StoreEntity storeEntity = cartEntity.getStore();

        UUID cartId = cartEntity.getId();
        List<CartItemQueryResult> queryResult = cartItemRepository.findCartItemsByCartId(cartId);
        List<CartItemsData> cartItemsDataList = queryResult.stream()
                .map(result -> new CartItemsData(
                        cartId,
                        result.itemId(),
                        result.productId(),
                        result.productName(),
                        result.productImage(),
                        result.quantity(),
                        result.price(),
                        result.price().multiply(BigDecimal.valueOf(result.quantity()))
                ))
                .toList();

        BigDecimal totalPrice = cartItemsDataList.stream()
                .map(CartItemsData::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new GetCartResponseDTO(storeEntity.getId(), storeEntity.getStoreName(), totalPrice, cartItemsDataList);
    }

    private CartEntity createCartRecord(UUID userId, UUID storeId) {
        UserEntity userEntity = userRepository.getReferenceById(userId);
        StoreEntity storeEntity = storeRepository.getReferenceById(storeId);
        CartEntity cartEntity = CartEntity.builder().user(userEntity).store(storeEntity).build();
        return cartRepository.save(cartEntity);
    }

    private MutateCartResponseDTO createCartItemRecord(UUID cartId, UUID productId, Integer quantity) {
        CartEntity cartEntity = cartRepository.getReferenceById(cartId);
        ProductEntity productEntity = productRepository.getReferenceById(productId);
        CartItemsEntity cartItemsEntity = CartItemsEntity.builder()
                .cart(cartEntity)
                .product(productEntity)
                .quantity(quantity)
                .build();
        cartItemRepository.save(cartItemsEntity);

        return new MutateCartResponseDTO(
                productEntity.getId(),
                productEntity.getName(),
                productEntity.getPrice().multiply(BigDecimal.valueOf(quantity)),
                quantity
        );
    }

    private MutateCartResponseDTO updateCartItemRecord(UUID productId, Integer quantity) {
        CartItemsEntity cartItemsEntity = cartItemRepository.findCartItemsEntityByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Cart item not found for product: " + productId));
        Integer currentQuantity = cartItemsEntity.getQuantity();
        cartItemsEntity.setQuantity(currentQuantity + quantity);
        cartItemRepository.save(cartItemsEntity);

        ProductEntity productEntity = cartItemsEntity.getProduct();
        return new MutateCartResponseDTO(
                productEntity.getId(),
                productEntity.getName(),
                productEntity.getPrice().multiply(BigDecimal.valueOf(quantity)),
                quantity
        );
    }

    public MutateCartResponseDTO mutateCart(MutateCartRequestDTO mutateCartRequestDTO) {
        UUID userId = SecurityUtil.getCurrentUserId();
        if (userId == null) throw new UnauthorizedException("User is not authenticated");

        // isUserHaveCart, which means if the user id exist in cart table, user already
        // added items to cart table
        boolean isUserHaveCart = cartRepository.existsByUserId(userId);

        // if user don't have cart, initiate new data
        if (!isUserHaveCart) {
            CartEntity createdCart = createCartRecord(userId, mutateCartRequestDTO.getStoreId());
            return createCartItemRecord(createdCart.getId(), mutateCartRequestDTO.getProductId(), mutateCartRequestDTO.getQuantity());
        }

        // then if user have cart ....
        // check if the added items is from same store or not
        UUID storeId = mutateCartRequestDTO.getStoreId();
        CartEntity cartEntity = cartRepository.findCartEntityByStoreId(storeId)
                .orElseThrow(() -> new NotFoundException("The item(s) added is from different store."));

        UUID productId = mutateCartRequestDTO.getProductId();
        Optional<CartItemsEntity> currentItem = cartItemRepository.findCartItemsEntityByProductId(productId);
        if (currentItem.isEmpty()) {
            return createCartItemRecord(cartEntity.getId(), mutateCartRequestDTO.getProductId(), mutateCartRequestDTO.getQuantity()); // create new record with same cart id
        } else {
            return updateCartItemRecord(mutateCartRequestDTO.getProductId(), mutateCartRequestDTO.getQuantity()); // update the quantity of the existing record
        }
    }

    @Transactional
    public MutateCartResponseDTO overrideDataCart(MutateCartRequestDTO mutateCartRequestDTO) {
        UUID userId = SecurityUtil.getCurrentUserId();
        if (userId == null) throw new UnauthorizedException("User is not authenticated");

        cartRepository.deleteCartEntityByUserId(userId);

        CartEntity createdCart = createCartRecord(userId, mutateCartRequestDTO.getStoreId());
        return createCartItemRecord(createdCart.getId(), mutateCartRequestDTO.getProductId(), mutateCartRequestDTO.getQuantity());
    }

    public void deleteCartItem(DeleteItemRequestDTO deleteItemRequestDTO) {
        UUID cartItemId = deleteItemRequestDTO.getCartItemId();
        UUID cartId = deleteItemRequestDTO.getCartId();
        cartItemRepository.deleteById(cartItemId);

        long remainingItemsCount = cartItemRepository.countByCartId(cartId);
        if (remainingItemsCount == 0) {
            cartRepository.deleteById(cartId);
        }
    }
}
