package de.rki.coronawarnapp.dccticketing.ui.certificateselection.cards

import android.view.ViewGroup
import androidx.core.view.isGone
import de.rki.coronawarnapp.R
import de.rki.coronawarnapp.databinding.DccTicketingValidCertificateHeaderCardBinding
import de.rki.coronawarnapp.dccticketing.core.transaction.DccTicketingValidationCondition
import de.rki.coronawarnapp.dccticketing.ui.certificateselection.DccTicketingCertificateItem
import de.rki.coronawarnapp.dccticketing.ui.certificateselection.DccTicketingCertificateSelectionAdapter
import de.rki.coronawarnapp.dccticketing.ui.certificateselection.certificateTypesText
import de.rki.coronawarnapp.dccticketing.ui.certificateselection.getFullName
import de.rki.coronawarnapp.util.lists.diffutil.HasPayloadDiffer

class DccTicketingValidCertificateHeaderCard(parent: ViewGroup) :
    DccTicketingCertificateSelectionAdapter.CertificatesItemVH<
        DccTicketingValidCertificateHeaderCard.Item,
        DccTicketingValidCertificateHeaderCardBinding>(
        layoutRes = R.layout.dcc_ticketing_valid_certificate_header_card,
        parent = parent
    ) {

    override val viewBinding: Lazy<DccTicketingValidCertificateHeaderCardBinding> = lazy {
        DccTicketingValidCertificateHeaderCardBinding.bind(itemView)
    }

    override val onBindData: DccTicketingValidCertificateHeaderCardBinding.(item: Item, payloads: List<Any>) -> Unit =
        { item, payloads ->
            val curItem = payloads.filterIsInstance<Item>().singleOrNull() ?: item
            val validationCondition = curItem.validationCondition

            with(allowedCertificates) {
                isGone = validationCondition?.type.isNullOrEmpty()
                text = validationCondition?.type?.let { context.certificateTypesText(it) }
            }
            with(birthDate) {
                isGone = validationCondition?.dob.isNullOrEmpty()
                text = validationCondition?.dob?.let {
                    context.getString(R.string.dcc_ticketing_certificate_birthday).format(it)
                }
            }

            with(name) {
                val fullName = getFullName(validationCondition?.fnt, validationCondition?.gnt)
                isGone = fullName.isEmpty()
                text = fullName
            }
        }

    data class Item(
        val validationCondition: DccTicketingValidationCondition?
    ) : DccTicketingCertificateItem, HasPayloadDiffer {
        override fun diffPayload(old: Any, new: Any): Any? = if (old::class == new::class) new else null
        override val stableId = Item::class.hashCode().toLong()
    }
}
